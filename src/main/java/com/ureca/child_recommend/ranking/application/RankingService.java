package com.ureca.child_recommend.ranking.application;


import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.ranking.dto.ContentLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ZSetOperations<String, Object> zSetOperations;
    private final ContentsRepository contentsRepository; // 콘텐츠 제목 조회
    private static final String REDIS_KEY = "content:likes"; // 상수

    // 좋아요 이벤트 처리 : 좋아요 카운팅
    public void countLike(Long contentId){
//        String redisKey = "content:likes";
        zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), 1);

    // 키의 TTL 설정 : 1시간 유지
        zSetOperations.getOperations().expire(REDIS_KEY, Duration.ofMinutes(5));

    }

 /*   // 좋아요 순위 조회 : 상위 N개 조회 (1시간 동안 집계된 순위)
    public Set<ZSetOperations.TypedTuple<Object>> getTopLikedContents(int limit){
        return zSetOperations.reverseRangeWithScores(REDIS_KEY, 0, limit - 1);
    }*/

    // 컨텐츠 제목을 보여주기 위한 리팩토링
    public List<ContentLikeDto> getTopLikedContents(int limit){
        Set<ZSetOperations.TypedTuple<Object>> topLikedSet = zSetOperations.reverseRangeByScoreWithScores(REDIS_KEY, 0, limit - 1);

        List<ContentLikeDto> topLikedContents = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : topLikedSet) {
            Long contentId = Long.valueOf(tuple.getValue().toString());
            String title = contentsRepository.findById(contentId)
                    .map(Contents::getTitle)
                    .orElse("Unknown");
            topLikedContents.add(new ContentLikeDto(title, tuple.getScore()));
        }
        return topLikedContents;
    }


}
