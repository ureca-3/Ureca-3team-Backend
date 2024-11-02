package com.ureca.child_recommend.event.application;

import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import com.ureca.child_recommend.event.domain.Event;
import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.infrastructure.ApplyLogRepository;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.infrastructure.LogHistoryRepository;
import com.ureca.child_recommend.event.presentation.dto.ApplyLogDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.user.domain.Users;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final String TOPIC_NAME = "apply-log-topic";
    private static final String USER_ID_LIST_KEY = "registered_userId"; // Redis 리스트 키
    private final LogHistoryRepository logHistoryRepository;

    //kafka 응모 요청
    public ApplyLog createAndSendApplyLog(ApplyLog applyLog) {
        kafkaTemplate.send(TOPIC_NAME, applyLog);
        return applyLog;
    }

//    public void executeWithLock(Long userId, ApplyLog applyLog) {
//        // 현재 시간을 가져옵니다
//        LocalTime now = LocalTime.now();
//
//        // 실행 조건: 13시에서 13시 10분 사이
//        LocalTime startTime = LocalTime.of(13, 0);
//        LocalTime endTime = LocalTime.of(13, 10);
//
//        // 시간대 확인
//        if (now.isAfter(startTime) && now.isBefore(endTime)) {
//            // 락 이름 설정
//            RLock lock = redissonClient.getLock("lock:" + userId);
//
//            try {
//                // 분산락을 획득 (10초 후 자동 해제)
//                if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
//                    try {
//                        // 중복 체크 및 등록
//                        boolean isRegistered = checkAndRegisterUserId(userId);
//
//                        // 등록이 성공한 경우에만 ApplyLog를 생성하고 전송
//                        if (isRegistered) {
//                            createAndSendApplyLog(applyLog);
//                        } else {
//                            throw new BusinessException(APPLY_EXISTS);
//                        }
//                    } finally {
//                        lock.unlock(); // 작업 종료 후 락 해제
//                        System.out.println("Lock released for user: " + userId + "!");
//                    }
//                } else {
//                    throw new BusinessException(APPLY_EXISTS);
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new BusinessException(APPLY_EXISTS);
//            }
//        } else {
//            // 시간 조건에 맞지 않을 경우 메시지 출력
//            throw new BusinessException(NOT_APPLY_TIME);
//        }
//    }

    //중복 처리 및 응모 요청 메소드
    public ApplyLogDto.Response checkAndRegisterUserId(Long userId, ApplyLogDto.Request requestDto, LocalDateTime now) {

        // 실행 조건: 13시에서 13시 10분 사이
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0));
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 10));



        // 시간대 확인
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            // Redis에서 사용자 ID가 있는지 확인
            Boolean userExists = redisTemplate.opsForList().range(USER_ID_LIST_KEY, 0, -1).stream()
                    .map(String::valueOf)
                    .anyMatch(existingUserId -> existingUserId.equals(String.valueOf(userId)));

            if (!userExists) {
                // 사용자 ID가 없다면 등록
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

                Event event = eventRepository.findEventByDate(LocalDate.now())
                        .orElseThrow(() -> new BusinessException(EVENT_NOT_FOUND));

                ApplyLog applyLog = ApplyLog.create(requestDto.getName(), requestDto.getPhone(),now,ApplyLogStatus.DEFAULT, user,event);
                redisTemplate.opsForList().rightPush(USER_ID_LIST_KEY, userId);
                System.out.println("User ID " + userId + " has been registered.");
                createAndSendApplyLog(applyLog);
                ApplyLogDto.Response responseDto = ApplyLogDto.Response.from(applyLog);

                return responseDto;
            } else {
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

                Event event = eventRepository.findEventByDate(LocalDate.now())
                        .orElseThrow(() -> new BusinessException(EVENT_NOT_FOUND));

                ApplyLog failedApplyLog = ApplyLog.create(requestDto.getName(), requestDto.getPhone(),now,ApplyLogStatus.ERROR, user,event);

                createAndSendApplyLog(failedApplyLog);
                throw new BusinessException(APPLY_EXISTS);
            }
        } else {
            throw new BusinessException(NOT_APPLY_TIME);
        }
    }

    public ApplyLogDto.Response imsi(Long userId, ApplyLogDto.Request requestDto, LocalDateTime now){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Event event = eventRepository.findEventByDate(LocalDate.now())
                .orElseThrow(() -> new BusinessException(EVENT_NOT_FOUND));

        ApplyLog applyLog = ApplyLog.create(requestDto.getName(), requestDto.getPhone(),now,ApplyLogStatus.DEFAULT, user,event);
        redisTemplate.opsForList().rightPush(USER_ID_LIST_KEY, userId);
        System.out.println("User ID " + userId + " has been registered.");
        createAndSendApplyLog(applyLog);
        ApplyLogDto.Response responseDto = ApplyLogDto.Response.from(applyLog);

        return responseDto;
    }
    //합격자 처리 메소드
    @Transactional
    public List<ApplyLog> setLogStatus() {
        List<ApplyLog> applyLogs =  applyLogRepository.findAllByOrderByLogAsc();
        int cnt = 0;
        List<ApplyLog> winnerLogs = new ArrayList<>();
        for(ApplyLog applylog : applyLogs) {
            if(applylog.getLog().getHour() >= 13 && cnt < 100 && applylog.getStatus() != ApplyLogStatus.ERROR) {
                cnt+=1;
                winnerLogs.add(applylog);
            } else if(cnt == 100) {break;}
        }
        return winnerLogs;
    }

    // 로그 이동 메소드
    @Transactional
    public void moveLogHistory() {
        List<ApplyLog> applyLogs = applyLogRepository.findAll();
        for(ApplyLog applylog : applyLogs) {
            LogHistory logHistory = LogHistory.builder()
                    .name(applylog.getName())
                    .phone(applylog.getPhone())
                    .log(applylog.getLog())
                    .status(applylog.getStatus())
                    .user(userRepository.findById(applylog.getUser().getId()).get())
                    .event(eventRepository.findEventById(applylog.getEvent().getId()).get())
                    .build();

            logHistoryRepository.save(logHistory);
        }
    }

    //db 전체삭제
    @Transactional
    public void deleteAllLog() {
        applyLogRepository.deleteAll();
        System.out.println("응모로그 전체 삭제 완료.");
    }

    //redis삭제 메소드
    @Transactional
    public void removeAllUserIds() {
        redisTemplate.delete(USER_ID_LIST_KEY);
        System.out.println("All user IDs have been removed.");
    }



}
