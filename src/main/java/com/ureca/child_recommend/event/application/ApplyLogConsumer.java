package com.ureca.child_recommend.event.application;

import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.infrastructure.ApplyLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplyLogConsumer {

    private final ApplyLogRepository applyLogRepository; // repository 주입

    @KafkaListener(topics = "apply-log-topic", groupId = "apply-log-group")
    public void consume(ApplyLog applyLog) {
        log.info("Received ApplyLog: {}", applyLog);

        // ApplyLog 테이블에 새로운 행을 추가
        applyLogRepository.save(applyLog);
        log.info("Saved ApplyLog to database: {}", applyLog);
    }
}
