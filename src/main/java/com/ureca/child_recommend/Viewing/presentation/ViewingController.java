package com.ureca.child_recommend.Viewing.presentation;

import com.ureca.child_recommend.Viewing.application.ViewingLikesService;
import com.ureca.child_recommend.Viewing.dto.ContentLikeDto;
import com.ureca.child_recommend.Viewing.infrastructure.UserChildRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/viewing")
public class ViewingController {

    private final ViewingLikesService viewingLikesService;
    private final UserChildRepository userChildRepository;

    // 상위 N개의 좋아요 콘텐츠 반환
    @GetMapping("/top-liked")
    public SuccessResponse<List<ContentLikeDto>> getTopLikedContents(@RequestParam(defaultValue = "10") int limit) {
        List<ContentLikeDto> topLikedContents = viewingLikesService.getTopLikedContents(limit);
        if (topLikedContents.isEmpty()) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE);
        }
        return SuccessResponse.success(topLikedContents);
    }

    // 최근 좋아요한 콘텐츠 목록 반환
    @GetMapping("/{childId}/recent-liked")
    public SuccessResponse<List<ContentLikeDto>> getRecentLikedContents(
            @PathVariable Long childId,
            @AuthenticationPrincipal Long userId
    ) {
        // userId에 속한 childId에 대해서만 목록 확인 가능 검증 로직
        if (userChildRepository.findByIdAndUserId(childId, userId).isEmpty()) {
            throw new BusinessException(CommonErrorCode.JWT_AUTHORIZATION_FAILED);
        }

        List<ContentLikeDto> recentLikedContents = viewingLikesService.getRecentLikedContents(childId);
        if (recentLikedContents.isEmpty()) {
            return SuccessResponse.success(List.of()); // 빈 리스트 반환
        }
        return SuccessResponse.success(recentLikedContents);
    }
}
