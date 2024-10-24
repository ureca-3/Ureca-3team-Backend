package com.ureca.child_recommend.ranking.presentation;

import com.ureca.child_recommend.ranking.application.RankingService;
import com.ureca.child_recommend.ranking.dto.ContentLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingController {

    private final RankingService rankingService;

@GetMapping("/top-liked")
public List<ContentLikeDto> getTopLikedContents(@RequestParam(defaultValue = "10") int limit){
    return rankingService.getTopLikedContents(limit); // 상위 N개의 좋아요 콘텐츠 반환
}

}
