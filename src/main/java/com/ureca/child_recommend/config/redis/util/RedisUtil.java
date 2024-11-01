package com.ureca.child_recommend.config.redis.util;

import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;

    public void setData(String key, String value, Long exprTime) {
        redisTemplate.opsForValue().set(key, value, exprTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }


    public void sendNotified(String topic, String message) {
        redisTemplate.convertAndSend(topic, message);
    }

    // 새 메서드 추가: Redis 리스트에 데이터를 삽입하는 메서드
    public void pushToList(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    // Redis 리스트에서 데이터를 범위로 가져오기
    public List<Object> getListRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public void saveBooks(String key, List<ContentsRecommendDto.Response.SimilarBookDto> books) {
        // JSON 배열 형태로 Redis에 저장
        redisTemplate.opsForValue().set(key, books);
    }

    public List<ContentsRecommendDto.Response.SimilarBookDto> getBooks(String key) {
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        if (objects == null) {
            return Collections.emptyList(); // 없으면 빈 리스트 반환
        }
        return objects.stream()
                .map(obj -> (ContentsRecommendDto.Response.SimilarBookDto) obj)
                .collect(Collectors.toList());
    }

    public void upDownScore(String key, String contentId, int count) {
        zSetOperations.incrementScore(key,contentId,count);
    }

    public void expireTodayLikeNum(String key, Duration min){
        redisTemplate.expire(key, min);

    }


}
