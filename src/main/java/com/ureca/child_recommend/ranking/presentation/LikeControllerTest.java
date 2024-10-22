package com.ureca.child_recommend.ranking.presentation;


import com.ureca.child_recommend.ranking.application.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class LikeControllerTest {

    private final RankingService rankingService;

    @PostMapping("/{contentId}/like")
    public void likeContent(@PathVariable Long contentId) {
        rankingService.countLike(contentId);
    }
}
