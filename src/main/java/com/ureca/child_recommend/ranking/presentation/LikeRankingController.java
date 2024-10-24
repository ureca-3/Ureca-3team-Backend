package com.ureca.child_recommend.ranking.presentation;


import com.ureca.child_recommend.ranking.application.RankingService;
import com.ureca.child_recommend.ranking.dto.LikeRequestDto;
import com.ureca.child_recommend.ranking.infrastructure.UserChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class LikeRankingController {

    private final RankingService rankingService;
    private final UserChildRepository userChildRepository; // 자녀와 부모 관계를 검증

    @PostMapping("/{contentId}/like")
    public void likeContent(@PathVariable Long contentId, @RequestBody LikeRequestDto requestDto, @AuthenticationPrincipal Long userId){
        Long childId = requestDto.getChildId(); // DTO에서 childId 추출

        // 해당 userId가 childId의 부모인지 검증 후 좋아요 카운팅 로직 수행
        if(userChildRepository.findByIdAndUserId(childId, userId).isPresent()){
            rankingService.countLike(contentId, childId);
        } else {
            throw new AccessDeniedException("본인의 자녀가 아니기에 권한이 없습니다.");
        }
    }

/*  @PostMapping("/{contentId}/like")
    public void likeContent(@PathVariable Long contentId, @RequestParam Long childId) {
        rankingService.countLike(contentId, childId); // 자녀 ID를 기반으로 좋아요 처리
    }*/
}
