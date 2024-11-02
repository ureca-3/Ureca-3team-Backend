package com.ureca.child_recommend.contents.presentation;

import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.contents.application.ContentsService;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("/api/v1/contents")
public class ContentsController {
    private final ContentsService contentsService;

    // contents 저장
    @PostMapping("/admin/save")
    public SuccessResponse<Long> saveContents(@AuthenticationPrincipal Long userId, @RequestPart ContentsDto.Request request, @RequestPart MultipartFile imageFile) {
        Contents content = contentsService.saveContents(userId, request, imageFile);
        return SuccessResponse.success(content.getId());
    }

    // 특정 contents 읽기
    @GetMapping("/read/{contentsId}")
    public SuccessResponse<ContentsDto.Response> readContent(@AuthenticationPrincipal Long userId, @PathVariable("contentsId") Long contentsId, @Parameter Long childId) {
        ContentsDto.Response content = contentsService.readContents(userId,childId,contentsId);
        return SuccessResponse.success(content);
    }

    // 특정 contents 수정 -> 수정 시 active status로
    @PatchMapping("/admin/update/{contentsId}")
    public SuccessResponse<ContentsDto.Response> updatecontent(@AuthenticationPrincipal Long userId, @PathVariable("contentsId") Long contentsId,
                                                               @RequestPart ContentsDto.Request request, MultipartFile newImage) {
        ContentsDto.Response content = contentsService.updateContents(contentsId, request, newImage);
        return SuccessResponse.success(content);
    }

    // 특정 contents 삭제
    @PatchMapping("/admin/delete/{contentsId}")
    public SuccessResponse<ContentsDto.Response> deleteContents(@AuthenticationPrincipal Long userId, @PathVariable("contentsId") Long contentsId) {
        return SuccessResponse.success(contentsService.deleteContents(contentsId));
    }

    @GetMapping("/search")
    public SuccessResponse<List<ContentsDto.Response>> searchContents(String keyword) {
        return SuccessResponse.success(contentsService.searchContents(keyword));
    }

    @GetMapping("/all")
    public SuccessResponse<Page<ContentsDto.Response>> getAllContents(@AuthenticationPrincipal Long userId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page,size);
        return SuccessResponse.success(contentsService.getAllContents(pageable));
    }

    /**
     * 24.10.24 작성자 : 정주현
     * 유저와 좋아요 한 도서와 비슷한 값 추출
     * @param userId  : token - 부모 아이디
     * @return
     */
    @GetMapping("/child/{childId}/recommendations")
    public SuccessResponse<List<ContentsRecommendDto.Response.SimilarBookDto>> searchBook(@AuthenticationPrincipal Long userId, @PathVariable("childId") Long childId){
        List<ContentsRecommendDto.Response.SimilarBookDto> response = contentsService.seachUserLikeContentsSim(userId,childId);
        return SuccessResponse.success(response);

    }

    /**
     * 24.11.02 작성자 : 정주현
     * 오늘 하루 좋아요 가장 많이 받은 도서 조회
     */
    @GetMapping("/most-liked-today")
    public SuccessResponse<List<ContentsRecommendDto.Response.SimilarBookDto>> getMostLikedBooksToday(){
        List<ContentsRecommendDto.Response.SimilarBookDto> response = contentsService.getMostLikedBooksToday();
        return SuccessResponse.success(response);
    }

    //    /**
//     * 24.10.24 작성자 : 정주현
//     * 도서 임베딩 값 삽입
//     * @param userId  : token - 부모 아이디
//     * @return
//     */
//    @GetMapping("/{contentsId}/embedding/generate")
//    public SuccessResponse<String> inputEmbeddingBook(@AuthenticationPrincipal Long userId,@PathVariable("contentsId") Long contentsId){
//       contentsService.inputEmbedding(userId,contentsId);
//        return SuccessResponse.successWithoutResult("성공");
//
//    }


}
