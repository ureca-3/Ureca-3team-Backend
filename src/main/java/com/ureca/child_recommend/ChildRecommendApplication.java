package com.ureca.child_recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
public class ChildRecommendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChildRecommendApplication.class, args);
	}

}
