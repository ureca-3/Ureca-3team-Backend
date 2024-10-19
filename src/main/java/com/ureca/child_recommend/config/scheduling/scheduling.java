package com.ureca.child_recommend.config.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
// 해당 클래스가 없으면 반복 X
public class scheduling {
    public static void main(String[] args) {
        SpringApplication.run(scheduling.class, args);
    }

}
