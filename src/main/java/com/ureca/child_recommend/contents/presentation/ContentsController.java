package com.ureca.child_recommend.contents.presentation;

import com.ureca.child_recommend.contents.application.ContentsService;
import com.ureca.child_recommend.contents.domain.Contents;
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
    @GetMapping("/save")
    public Long saveContents(@AuthenticationPrincipal Long userId, @RequestBody ContentsDto.Request request) {
        return contentsService.saveContents(request);
    }

    // 특정 contents 읽기
    @GetMapping("/read")
    public Contents readContent(@AuthenticationPrincipal Long userId, @RequestParam("contentsId") Long contentsId) {
        Contents content = contentsService.readContents(contentsId);
        return content;
    }
    
    // 특정 contents 수정
    @PutMapping("/update")
    public Contents updatecontent(@AuthenticationPrincipal Long userId, @RequestParam("contentsId") Long contentsId,
                                  @RequestBody ContentsDto.Request request) {
        Contents content = contentsService.updateContents(contentsId, request);
        return content;
    }

    // 특정 contents 삭제
    @PutMapping("/delete")
    public SuccessResponse<ContentsDto.Response> deleteContents(@AuthenticationPrincipal Long userId, @RequestParam("contentsId") Long contentsId){
        return SuccessResponse.success(contentsService.deleteContents(contentsId));
    }
}
