package com.ureca.child_recommend.child.presentation;

import com.ureca.child_recommend.child.application.ChildService;
import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.relation.application.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChildController {
    private final ChildService childService;
    private final FeedBackService feedBackService;



    // 자녀 프로필 생성 API
    @PostMapping("/child")
    public SuccessResponse<Long> createChildProfile(@AuthenticationPrincipal Long userId,
                                                    @RequestPart ChildDto.Request childRequest,
                                                    @RequestPart MultipartFile image) {

            Long child_id = childService.createChildProfile(userId, childRequest, image); // 자녀 프로필 생성
            return SuccessResponse.success(child_id); // 성공 메시지 반환
    }

    // 내 자녀 조회 API
    @GetMapping("/child")
    public SuccessResponse<List<ChildDto.Response>> getAllChildren(@AuthenticationPrincipal Long userId) {
        List<ChildDto.Response> childList = childService.getAllChildren(userId);
        return SuccessResponse.success(childList);
    }


    //상세조회
    @GetMapping("/child/{child_id}")
    public SuccessResponse<ChildDto.Response> getChildById(@AuthenticationPrincipal Long userId, @PathVariable("child_id") Long childId) {
        Child child = childService.getChildById(userId, childId);
        ChildDto.Response childDto = ChildDto.Response.fromEntity(child);
        return SuccessResponse.success(childDto);
    }

    // 자녀 프로필 수정
    @PatchMapping("/child/{child_id}")
    public SuccessResponse<ChildDto.Response> updateChild(@PathVariable("child_id") Long childId,
                                                         @RequestBody ChildDto.Request childRequest) {

        ChildDto.Response updatedChild = childService.updateChild(childId, childRequest);
        return SuccessResponse.success(updatedChild);
    }

    // 자녀 프로필 status수정(논리적 삭제)
    @PatchMapping("/child/status/{child_id}")
    public SuccessResponse<String> deleteChild(@PathVariable("child_id") Long childId) {
        childService.deleteChild(childId);
        return SuccessResponse.successWithoutResult(null);
    }

    //  프로필 사진 수정 처리
    @PatchMapping("/child/picture/{child_id}")
    public SuccessResponse<String> updateChildProfileUrl(@PathVariable("child_id") Long childId, @RequestPart MultipartFile profileUrl) throws IOException {
        childService.updateChildProfile(childId, profileUrl);
        return SuccessResponse.successWithoutResult(null);
    }

    // 자녀 MBTI 조회 API
    @GetMapping("/child/mbti/{child_id}")
    public SuccessResponse<ChildMbtiScore> getChildMbti(@PathVariable("child_id") Long childId) {
        ChildMbtiScore mbtiResult = childService.getChildMbti(childId); // 자녀의 MBTI 조회
        return SuccessResponse.success(mbtiResult); // 성공 시 MBTI 결과 반환
    }

    // 좋아요한 컨텐츠 조회
    @GetMapping("/child/feedback")
    public SuccessResponse<List<String>> getLikedContents(@RequestBody Long childId) {
        List<String> likedContents = feedBackService.getLikedContents(childId);
        return SuccessResponse.success(likedContents);
    }

    // 최근 감상한 컨텐츠 조회
    @GetMapping("/child/recentcontents")
    public SuccessResponse<List<Contents>> getRecentContents( @RequestBody Long childId) {
        List<Contents> recentContents = feedBackService.getRecentContents(childId);
        return SuccessResponse.success(recentContents);
    }


//    /**
//     * 24.10.24 작성자 : 정주현
//     * 특정 유저 자녀의 (나이,성별,mbti)로부터 임베딩 벡터 값 가져와서 db에 저장
//     * @param userId :  token - 부모 아이디
//     * @param childId : 자녀 아이디
//     */
//    @GetMapping("child/{childId}/embedding/generate")
//    public SuccessResponse<String> inputEmbeddingChild(@AuthenticationPrincipal Long userId,  @PathVariable Long childId){
//        childService.inputEmbedding(userId,childId);
//        return SuccessResponse.success("성공");
//    }

    /**
     * 24.10.24 작성자 : 정주현
     * 특정 자녀와 (나이,성별,mbti)가 비슷한 유저 목록으로부터 책 목록 추출
     * @param userId : token - 부모 아이디
     * @param childId : 자녀 아이디
     * @return
     */
    @GetMapping("child/{childId}/embedding")
    public SuccessResponse<List<ContentsRecommendDto.Response.SimilarBookDto>> getSimilarUsers(@AuthenticationPrincipal Long userId, @PathVariable Long childId){
        List<ContentsRecommendDto.Response.SimilarBookDto> response = childService.getSimilarUsersBooks(userId, childId);
        return SuccessResponse.success(response);
    }
}
