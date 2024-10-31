package com.ureca.child_recommend.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic applyLogTopic() {
        return new NewTopic("apply-log-topic", 8, (short) 1);  // 파티션과 복제본 수 설정
    }
}
