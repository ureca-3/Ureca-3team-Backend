package com.ureca.child_recommend.Viewing.application;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.Viewing.dto.ContentLikeDto;
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
public class RankingService {

    private final ZSetOperations<String, Object> zSetOperations;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ContentsRepository contentsRepository; // 콘텐츠 제목 조회

    private static final String REDIS_KEY = "content:likes"; // 현재 좋아요 저장 키
    private static final String CHILD_LIKED_KEY = "child:liked"; // 자녀의 좋아요 상태 저장 키

    // 좋아요 이벤트 처리 : 좋아요 카운팅
    public void countLike(Long contentId, Long childId) {
        // 자녀의 '좋아요' 상태를 기록할 Redis 키
        String childLikedKey = CHILD_LIKED_KEY + ":" + childId + ":" + contentId;

        // 해당 contentId가 존재하는지 확인
        Contents contents = contentsRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE)); // 콘텐츠가 없으면 예외 처리

        // 자녀가 이미 '좋아요'를 눌렀는지 확인
        Boolean hasLiked = zSetOperations.getOperations().hasKey(childLikedKey);

        if (Boolean.TRUE.equals(hasLiked)) {
            // 이미 '좋아요'를 누른 상태면 '좋아요' 취소 처리
            zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), -1); // 카운팅 -1
            redisTemplate.delete(childLikedKey); // 자녀의 좋아요 상태 삭제
        } else {
            // '좋아요'를 처음 누른 경우
            zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), 1); // 카운팅 +1
            redisTemplate.opsForValue().set(childLikedKey, "true"); // 자녀의 좋아요 상태 저장

            // 자정까지 남은 시간을 계산하여 TTL 설정
            long minutesUntilMidnight = calcMinutesUntilMidnight();
            redisTemplate.expire(REDIS_KEY, Duration.ofMinutes(minutesUntilMidnight));
        }
    }

    // 자정까지 남은 시간을 계산하는 메서드 (분 단위)
    private long calcMinutesUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1); // 자정 시간
        return ChronoUnit.MINUTES.between(now, midnight); // 자정까지 남은 분 계산
    }

    // 좋아요 순위 조회 및 콘텐츠 제목 반환
    public List<ContentLikeDto> getTopLikedContents(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> topLikedSet = zSetOperations.reverseRangeByScoreWithScores(REDIS_KEY, 0, limit - 1);

        if (topLikedSet == null || topLikedSet.isEmpty()) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE); // 데이터가 없을 경우
        }

        List<ContentLikeDto> topLikedContents = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : topLikedSet) {
            Long contentId = Long.valueOf(tuple.getValue().toString());
            Contents content = contentsRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE)); // 존재하지 않는 콘텐츠는 예외 처리

            topLikedContents.add(new ContentLikeDto(
                    content.getTitle(),
                    content.getId(),
                    content.getPosterUrl(),
                    tuple.getScore()
            ));
        }
        return topLikedContents; // 현재 좋아요 상위 콘텐츠 목록
    }
}