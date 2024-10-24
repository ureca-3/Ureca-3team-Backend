package com.ureca.child_recommend.contents.presentation;

import com.ureca.child_recommend.contents.application.ContentsService;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("/api/v1/contents")
public class ContentsController {
    private final ContentsService contentsService;

    // contents 저장
    @PostMapping("/save")
    public SuccessResponse<String> saveContents(@AuthenticationPrincipal Long userId, @RequestBody ContentsDto.Request request) {
        contentsService.saveContents(userId, request);
        return SuccessResponse.successWithoutResult("콘텐츠 저장을 완료했습니다.");
    }

    // 특정 contents 읽기
    @GetMapping("/read/{contentsId}")
    public SuccessResponse<ContentsDto.Response> readContent(@AuthenticationPrincipal Long userId, @PathVariable("contentsId") Long contentsId) {
        ContentsDto.Response content = contentsService.readContents(contentsId);
        return SuccessResponse.success(content);
    }
    
    // 특정 contents 수정
    @PatchMapping("/update/{contentsId}")
    public SuccessResponse<ContentsDto.Response> updatecontent(@AuthenticationPrincipal Long userId, @PathVariable("contentsId") Long contentsId,
                                  @RequestBody ContentsDto.Request request) {
        ContentsDto.Response content = contentsService.updateContents(contentsId, request);
        return SuccessResponse.success(content);
    }

    // 특정 contents 삭제
    @PatchMapping("/delete/{contentsId}")
    public SuccessResponse<ContentsDto.Response> deleteContents(@AuthenticationPrincipal Long userId, @PathVariable("contentsId") Long contentsId) {
        return SuccessResponse.success(contentsService.deleteContents(contentsId));
    }
}
