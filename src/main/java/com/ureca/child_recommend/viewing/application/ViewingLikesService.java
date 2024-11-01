package com.ureca.child_recommend.viewing.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.viewing.dto.ContentLikeDto;
import com.ureca.child_recommend.viewing.infrastructure.RankingFeedBackRepository;
import com.ureca.child_recommend.relation.domain.FeedBack;
import com.ureca.child_recommend.relation.domain.Enum.FeedBackType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ViewingLikesService {


    private final ZSetOperations<String, Object> zSetOperations;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ContentsRepository contentsRepository;
    private final RankingFeedBackRepository rankingFeedBackRepository;
    private final ChildRepository childRepository;

    private static final String REDIS_KEY = "content:likes";
    private static final String CHILD_LIKED_KEY = "child:liked";
    private static final String RECENT_LIKES_KEY = "recent:likes";

    // 좋아요 이벤트 처리 : 좋아요 카운팅
    @Transactional
    public void countLike(Long contentId, Long childId) {
        String childLikedKey = CHILD_LIKED_KEY + ":" + childId + ":" + contentId;

        Contents contents = contentsRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE));

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        // 좋아요 상태 확인
        Boolean hasLiked = zSetOperations.getOperations().hasKey(childLikedKey);

        if (Boolean.TRUE.equals(hasLiked)) {
            zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), -1);
            redisTemplate.delete(childLikedKey);
            rankingFeedBackRepository.deleteByChildIdAndContentsId(childId, contentId); // PostgreSQL 기록 삭제

            // Redis에서 최근 좋아요 목록에서 제거
            redisTemplate.opsForList().remove(RECENT_LIKES_KEY + ":" + childId, 1, contentId.toString());

        } else {
            zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), 1);
            redisTemplate.opsForValue().set(childLikedKey, "true");
            redisTemplate.expire(REDIS_KEY, Duration.ofMinutes(calcMinutesUntilMidnight()));

            rankingFeedBackRepository.save(
                    FeedBack.builder()
                            .child(child)
                            .contents(contents)
                            .type(FeedBackType.LIKE)
                            .build()
            );

            // Redis에 최근 좋아요 목록 추가 (중복 확인)
            List<Object> recentLikes = redisTemplate.opsForList().range(RECENT_LIKES_KEY + ":" + childId, 0, -1);
            if (!recentLikes.contains(contentId.toString())) {
                redisTemplate.opsForList().leftPush(RECENT_LIKES_KEY + ":" + childId, contentId.toString());
                redisTemplate.opsForList().trim(RECENT_LIKES_KEY + ":" + childId, 0, 9); // 최대 10개 유지
            }
        }
    }

    private long calcMinutesUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        return ChronoUnit.MINUTES.between(now, midnight);
    }

    // 좋아요 순위 조회 및 콘텐츠 제목 반환
    public List<ContentLikeDto> getTopLikedContents(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> topLikedSet = zSetOperations.reverseRangeByScoreWithScores(REDIS_KEY, 0, limit - 1);

        if (topLikedSet == null || topLikedSet.isEmpty()) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE);
        }

        List<ContentLikeDto> topLikedContents = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : topLikedSet) {
            Long contentId = Long.valueOf(tuple.getValue().toString());
            Contents content = contentsRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE));

            topLikedContents.add(new ContentLikeDto(
                    content.getTitle(),
                    content.getId(),
                    content.getPosterUrl(),
                    tuple.getScore()
            ));
        }
        return topLikedContents;
    }

    // 최근 좋아요 누른 콘텐츠 목록 반환
    public List<ContentLikeDto> getRecentLikedContents(Long childId) {
        List<Object> recentLikes = redisTemplate.opsForList().range(RECENT_LIKES_KEY + ":" + childId, 0, 9);
        List<ContentLikeDto> recentLikedContents = new ArrayList<>();
        if (recentLikes != null) {
            for (Object contentIdObj : recentLikes) {
                Long contentId = Long.valueOf(contentIdObj.toString());
                Contents content = contentsRepository.findById(contentId)
                        .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE));

                recentLikedContents.add(new ContentLikeDto(
                        content.getTitle(),
                        content.getId(),
                        content.getPosterUrl(),
                        zSetOperations.score(REDIS_KEY, contentId.toString())
                ));
            }
        }
        return recentLikedContents;
    }
}