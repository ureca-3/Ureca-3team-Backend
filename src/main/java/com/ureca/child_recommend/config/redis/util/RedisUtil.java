package com.ureca.child_recommend.config.redis.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;


    public void setData(String key, String value, Long exprTime) {
        redisTemplate.opsForValue().set(key, value, exprTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteDate(String key) {
        redisTemplate.delete(key);
    }

    public void sendNotified(String topic ,String message){
        redisTemplate.convertAndSend(topic, message);
    }

}
