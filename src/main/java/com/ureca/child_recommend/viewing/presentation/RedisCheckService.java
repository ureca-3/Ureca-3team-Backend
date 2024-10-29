package com.ureca.child_recommend.viewing.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCheckService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis 연결 확인 메서드
    public void checkRedisData() {
        // 예시 데이터 삽입
        redisTemplate.opsForValue().set("testKey", "testValue");

        // 데이터 확인
        String result = (String) redisTemplate.opsForValue().get("testKey");
        System.out.println("Redis에 저장된 값: " + result);

        // 데이터 삭제 후 확인
        redisTemplate.delete("testKey");
    }
}
