package com.ureca.child_recommend.event.application;

import com.ureca.child_recommend.event.domain.ApplyLog;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyLogService {

    private final KafkaTemplate<String, ApplyLog> kafkaTemplate;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 추가
    private static final String TOPIC_NAME = "apply-log-topic";
    private static final String USER_ID_LIST_KEY = "registered_user_ids"; // Redis 리스트 키

    public ApplyLog createAndSendApplyLog(ApplyLog applyLog) {
        // ApplyLog 테이블에 객체 저장하는 로직
        kafkaTemplate.send(TOPIC_NAME, applyLog);  // Kafka에 전송
        return applyLog;
    }

    public void executeWithLock(Long userId, ApplyLog applyLog) {
        // 사용자의 식별자를 기반으로 락 이름을 설정합니다.
        RLock lock = redissonClient.getLock("lock:" + userId);

        try {
            // 분산락을 획득합니다. 10초 후 자동으로 해제됩니다.
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    // 중복 체크 및 등록 메소드 호출
                    boolean isRegistered = checkAndRegisterUserId(userId);

                    // 등록이 성공한 경우에만 ApplyLog를 생성하고 전송합니다.
                    if (isRegistered) {
                        createAndSendApplyLog(applyLog);
                    } else {
                        // 중복된 경우 작업을 블락하거나 다른 처리를 할 수 있음
                        System.out.println("Operation blocked for user: " + userId + " due to duplication.");
                    }
                } finally {
                    lock.unlock(); // 작업이 끝나면 락을 해제합니다.
                    System.out.println("Lock released for user: " + userId + "!");
                }
            } else {
                System.out.println("Could not acquire lock for user: " + userId + ", try again later.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Lock acquisition interrupted for user: " + userId + ": " + e.getMessage());
        }
    }


    private boolean checkAndRegisterUserId(Long userId) {
        // Redis에서 사용자 ID가 있는지 확인
        Boolean userExists = redisTemplate.opsForList().range(USER_ID_LIST_KEY, 0, -1).stream()
                .map(String::valueOf)
                .anyMatch(existingUserId -> existingUserId.equals(String.valueOf(userId)));

        if (!userExists) {
            // 사용자 ID가 없다면 등록
            redisTemplate.opsForList().rightPush(USER_ID_LIST_KEY, userId);
            System.out.println("User ID " + userId + " has been registered.");
            return true; // 등록 성공 시 true 반환
        } else {
            System.out.println("User ID " + userId + " already exists.");
            return false; // 중복 시 false 반환
        }
    }

}
