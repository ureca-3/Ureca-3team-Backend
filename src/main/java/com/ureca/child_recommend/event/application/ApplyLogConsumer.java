package com.ureca.child_recommend.event.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.infrastructure.ApplyLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplyLogConsumer {

    private final ApplyLogRepository applyLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "apply-log-topic", groupId = "apply-log-group")
    public void consume(String message) {
        try {
            // JSON 문자열을 ApplyLog 객체로 변환
            ApplyLog applyLog = objectMapper.readValue(message, ApplyLog.class);
            log.info("Received ApplyLog: {}", applyLog);

            // ApplyLog 테이블에 새로운 행을 추가
            applyLogRepository.save(applyLog);
            log.info("Saved ApplyLog to database: {}", applyLog);
        } catch (Exception e) {
            log.error("Failed to deserialize ApplyLog message", e);
        }
    }
}
