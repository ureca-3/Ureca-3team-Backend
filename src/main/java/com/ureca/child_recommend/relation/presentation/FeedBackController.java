package com.ureca.child_recommend.relation.presentation;

import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.relation.application.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
public class FeedBackController {
    private final FeedBackService feedBackService;

    @PostMapping("/child/{childId}/content/{contentsId}/like")
    SuccessResponse<String> likeFeedBack(@AuthenticationPrincipal Long userId, @PathVariable("childId") Long childId, @PathVariable("contentsId") Long contentsId){
        feedBackService.likeFeedback(userId,childId,contentsId);
        return SuccessResponse.successWithoutResult("성공");
    }

    @PostMapping("/child/{childId}/content/{contentsId}/dislike")
    SuccessResponse<String> dislikeFeedBack(@AuthenticationPrincipal Long userId, @PathVariable("childId") Long childId, @PathVariable("contentsId") Long contentsId){
        feedBackService.dislikeFeedback(userId,childId,contentsId);
        return SuccessResponse.successWithoutResult("성공");
    }

}
