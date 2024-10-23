package com.ureca.child_recommend.child.presentation;

import com.ureca.child_recommend.child.application.ChildService;
import com.ureca.child_recommend.child.application.FileService;
import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final FileService fileService;

    //공통 사진 저장 API(URL 반환)
    @PostMapping("/picture")
    public SuccessResponse<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
            String fileUrl = fileService.uploadFile(file);
            return SuccessResponse.success(fileUrl);
    }

    // 자녀 프로필 생성 API
    @PostMapping("/child")
    public SuccessResponse<String> createChildProfile(@AuthenticationPrincipal Long userId,
                                                      @RequestBody ChildDto.Request childRequest) {

            childService.createChildProfile(userId, childRequest); // 자녀 프로필 생성
            return SuccessResponse.successWithoutResult(null); // 성공 메시지 반환
    }

    // 내 자녀 조회 API
    @GetMapping("/child")
    public SuccessResponse<List<ChildDto.Response>> getAllChildren(@AuthenticationPrincipal Long userId) {
        List<ChildDto.Response> childList = childService.getAllChildren(userId);
        return SuccessResponse.success(childList);
    }



    @GetMapping("/child/{child_id}")//상세조회
    public SuccessResponse<ChildDto.Response> getChildById(@PathVariable("child_id") Long childId) {
        Child child = childService.getChildById(childId);
        ChildDto.Response childDto = ChildDto.Response.fromEntity(child);
        return SuccessResponse.success(childDto);
    }

    // 자녀 프로필 수정
    @PatchMapping("/child/{child_id}")
    public SuccessResponse<ChildDto.Response> updateChild( @PathVariable("child_id") Long childId,
                                                         @RequestBody ChildDto.Request childRequest) {
        // 수정 처리
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
    public SuccessResponse<String> deleteChild(@PathVariable("child_id") Long childId, @RequestBody String profileUrl) throws IOException {
        childService.updateChildProfile(childId, profileUrl);
        return SuccessResponse.successWithoutResult(null);
    }
//
    // 자녀 MBTI 조회 API
    @GetMapping("/child/mbti/{child_id}")
    public SuccessResponse<ChildMbtiScore> getChildMbti(@PathVariable("child_id") Long childId) {
        ChildMbtiScore mbtiResult = childService.getChildMbti(childId); // 자녀의 MBTI 조회
        return SuccessResponse.success(mbtiResult); // 성공 시 MBTI 결과 반환
    }

}
