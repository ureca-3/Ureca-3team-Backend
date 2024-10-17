package com.ureca.child_recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChildRecommendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChildRecommendApplication.class, args);
	}

}
