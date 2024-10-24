package com.ureca.child_recommend.ranking.application;


import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.ranking.dto.ContentLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    private static final String BACKUP_KEY_PREFIX = "content:likes:backup"; // 이전 날 좋아요 백업 키
    private static final String CHILD_LIKED_KEY = "child:liked"; // 자녀의 좋아요 상태 저장 키


    // 좋아요 이벤트 처리 : 좋아요 카운팅
    // contentId : 좋아요를 누른 콘텐츠의 ID, childId : 좋아요를 누른 자녀의 ID
    public void countLike(Long contentId, Long childId){
    // 자녀의 '좋아요' 상태를 기록할 Redis 키
        String childLikedKey = CHILD_LIKED_KEY + ":" + childId + ":" + contentId;
    // 해당 contentId가 존재하는지 확인
        contentsRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE)); // 콘텐츠가 없으면 예외 처리


    // 자녀가 이미 '좋아요'를 눌렀는지 확인
        Boolean hasLiked = zSetOperations.getOperations().hasKey(childLikedKey);

        if(Boolean.TRUE.equals(hasLiked)){
            // 이미 '좋아요'를 누른 상태면은 '좋아요' 취소 처리
            zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), -1); // 카운팅 -1
            redisTemplate.delete(childLikedKey); // 자녀의 좋아요 상태 삭제
        } else {
            // '좋아요'를 처음 누른 경우
            zSetOperations.incrementScore(REDIS_KEY, contentId.toString(), 1); // 카운팅 +1
            redisTemplate.opsForValue().set(childLikedKey, "true"); // 자녀의 좋아요 상태 저장
//            redisTemplate.opsForValue().set(childLikedKey, Boolean.toString(true)); // 자녀의 좋아요 상태 저장 refactor
        }
/*    // 키의 TTL 설정 : 1시간 유지
     zSetOperations.getOperations().expire(REDIS_KEY, Duration.ofHours(1)); */
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 00:00:00에 실행
    public void resetDailyRankig(){
        // 백업 키 생성 (ex content:likes:backup:1487 형태로 timestamp 사용
        String backupKey = BACKUP_KEY_PREFIX + System.currentTimeMillis();
        // 현재 좋아요 데이터를 백업 (키 이름을 변경),
        zSetOperations.getOperations().rename(REDIS_KEY, backupKey);
        // Redis에 현재 좋아요 데이터 삭제 (랭킹 초기화)
        zSetOperations.getOperations().delete(REDIS_KEY);
        // 로그 확인
        System.out.println("contents ranking system reset, backup saved with key");
    }

    public List<ContentLikeDto> getPreviousDayTopLikedContents(int limit, long timestamp){
        // 백업된 랭킹 데이터를 조회하는 키
        String backupKey = BACKUP_KEY_PREFIX + timestamp;

        // 백업된 좋아요 순위를 조회
        Set<ZSetOperations.TypedTuple<Object>> topLikedSet = zSetOperations.reverseRangeByScoreWithScores(backupKey, 0, limit - 1);

        if(topLikedSet == null || topLikedSet.isEmpty()){
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE); // 데이터가 없을 경우
        }

        // 좋아요 순위를 DTO로 변환하여 반환
        List<ContentLikeDto> topLikedContents = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : topLikedSet) {
            Long contentId = Long.valueOf(tuple.getValue().toString()); // 콘텐츠 ID 추출
            String title = contentsRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE)) // 존재하지 않는 콘텐츠는 예외 처리
                    .getTitle(); // 제목 조회
//            String title = contentsRepository.findById(contentId).map(c -> c.getTitle()).orElse("Unknown"); // 제목 조회 refactor
            topLikedContents.add(new ContentLikeDto(title, tuple.getScore())); // DTO로 변환해 리스트에 추가
        }
        return topLikedContents; // 전날의 좋아요 상위 콘텐츠 목록
    }

    // 좋아요 순위 조회 및 콘텐츠 제목 반환
    public List<ContentLikeDto> getTopLikedContents(int limit){
        Set<ZSetOperations.TypedTuple<Object>> topLikedSet = zSetOperations.reverseRangeByScoreWithScores(REDIS_KEY, 0, limit - 1);

        if (topLikedSet == null || topLikedSet.isEmpty()) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE); // 데이터가 없을 경우
        }

        List<ContentLikeDto> topLikedContents = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : topLikedSet) {
            Long contentId = Long.valueOf(tuple.getValue().toString());
            String title = contentsRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE)) // 존재하지 않는 콘텐츠는 예외 처리
                    .getTitle(); // 제목 조회
//            String title = contentsRepository.findById(contentId).map(c -> c.getTitle()).orElse("Unknown"); // 제목 조회 refactor
            topLikedContents.add(new ContentLikeDto(title, tuple.getScore()));
        }
        return topLikedContents; // 현재 좋아요 상위 콘텐츠 목록
    }


}
