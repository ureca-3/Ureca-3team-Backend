package com.ureca.child_recommend.event.application;

import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import com.ureca.child_recommend.event.domain.WinnerLog;
import com.ureca.child_recommend.event.infrastructure.ApplyLogRepository;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.infrastructure.WinnerLogRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplyLogService {

    private final KafkaTemplate<String, ApplyLog> kafkaTemplate;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 추가
    private final ApplyLogRepository applyLogRepository;
    private final WinnerLogRepository winnerLogRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final String TOPIC_NAME = "apply-log-topic";
    private static final String USER_ID_LIST_KEY = "registered_user_ids"; // Redis 리스트 키

    public ApplyLog createAndSendApplyLog(ApplyLog applyLog) {
        kafkaTemplate.send(TOPIC_NAME, applyLog);
        return applyLog;
    }

    public void executeWithLock(Long userId, ApplyLog applyLog) {
        // 현재 시간을 가져옵니다
        LocalTime now = LocalTime.now();

        // 실행 조건: 13시에서 13시 10분 사이
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 10);

        // 시간대 확인
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            // 락 이름 설정
            RLock lock = redissonClient.getLock("lock:" + userId);

            try {
                // 분산락을 획득 (10초 후 자동 해제)
                if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                    try {
                        // 중복 체크 및 등록
                        boolean isRegistered = checkAndRegisterUserId(userId);

                        // 등록이 성공한 경우에만 ApplyLog를 생성하고 전송
                        if (isRegistered) {
                            createAndSendApplyLog(applyLog);
                        } else {
                            throw new BusinessException(APPLY_EXISTS);
                        }
                    } finally {
                        lock.unlock(); // 작업 종료 후 락 해제
                        System.out.println("Lock released for user: " + userId + "!");
                    }
                } else {
                    throw new BusinessException(APPLY_EXISTS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(APPLY_EXISTS);
            }
        } else {
            // 시간 조건에 맞지 않을 경우 메시지 출력
            throw new BusinessException(NOT_APPLY_TIME);
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

    public List<ApplyLog> setLogStatus() {
        List<ApplyLog> applyLogs =  applyLogRepository.findAllByOrderByLogAsc();
        int cnt = 0;
        List<ApplyLog> winnerLogs = new ArrayList<>();
        for(ApplyLog applylog : applyLogs) {
            if(applylog.getLog().getHour() >= 13 && cnt < 100) {
                cnt+=1;
                winnerLogs.add(applylog);
            } else if(cnt == 100) {break;}
        }
        return winnerLogs;
    }

    public void moveWinnerLog(List<ApplyLog> applyLogs) {
        for(ApplyLog applylog : applyLogs) {
            WinnerLog winnerLog = WinnerLog.builder()
                    .name(applylog.getName())
                    .phone(applylog.getPhone())
                    .log(applylog.getLog())
                    .user(userRepository.findById(applylog.getUser().getId()).get())
                    .event(eventRepository.findEventById(applylog.getEvent().getId()).get())
                    .build();

            winnerLogRepository.save(winnerLog);
        }
    }

    @Transactional
    public void deleteAllLog() {
        applyLogRepository.deleteAll();
        System.out.println("응모로그 전체 삭제 완료.");
    }

}
