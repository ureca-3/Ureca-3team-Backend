package com.ureca.child_recommend.ranking.presentation;

import com.ureca.child_recommend.ranking.application.RankingService;
import com.ureca.child_recommend.ranking.dto.ContentLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingController {

    private final RankingService rankingService;
/*
    // 1시간 동안 상위 '좋아요' 콘텐츠 조회 API
    @GetMapping("/top-liked")
    public Set<ZSetOperations.TypedTuple<Object>> getTopLikedContents(
            @RequestParam(defaultValue = "10") int limit){
        return rankingService.getTopLikedContents(limit);
    }*/

    @GetMapping("/top-liked")
    public List<ContentLikeDto> getTopLikedContents(@RequestParam(defaultValue = "10") int limit){
        // DTO 형식으로 반환
        return rankingService.getTopLikedContents(limit);
    }
}
