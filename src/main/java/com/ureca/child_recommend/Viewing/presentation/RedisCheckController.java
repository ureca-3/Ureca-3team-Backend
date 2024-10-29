package com.ureca.child_recommend.Viewing.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/redis")
public class RedisCheckController {

    private final RedisCheckService redisCheckService;

    // Redis 연결 확인용 엔드포인트
    @GetMapping("/check")
    public String checkRedisConnection() {
        redisCheckService.checkRedisData();
        return "Redis 데이터 확인 완료";
    }
}