package com.fastcampus.programming.dmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing -> 테스트할 때 모든 bean 을 추가해주지 않으면 에러 발생
// -> config 파일로 분리
@SpringBootApplication
public class DMakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DMakerApplication.class, args);
		System.out.println("fight!"); // 달아놓는 것이 편하당
	}

}
