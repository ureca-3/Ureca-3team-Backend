package com.ureca.child_recommend.viewing.presentation;

import com.ureca.child_recommend.viewing.application.ViewingLikesService;
import com.ureca.child_recommend.viewing.dto.LikeRequestDto;
import com.ureca.child_recommend.viewing.infrastructure.UserChildRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
public class LikeController {

    private final ViewingLikesService viewingLikesService;
    private final UserChildRepository userChildRepository; // 자녀와 부모 관계를 검증

    @PostMapping("/{contentId}/like")
    public SuccessResponse<String> likeContent(@PathVariable Long contentId,
                                               @RequestBody LikeRequestDto requestDto,
                                               @AuthenticationPrincipal Long userId){
        Long childId = requestDto.getChildId(); // DTO에서 childId 추출

        // 해당 userId가 childId의 부모인지 검증 후 좋아요 카운팅 로직 수행
        if(userChildRepository.findByIdAndUserId(childId, userId).isPresent()){
            viewingLikesService.countLike(contentId, childId);
            return SuccessResponse.successWithoutResult(null);
        } else {
            throw new BusinessException(CommonErrorCode.JWT_AUTHORIZATION_FAILED);
        }
    }


}
