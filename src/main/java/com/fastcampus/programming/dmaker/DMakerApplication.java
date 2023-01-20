package com.fastcampus.programming.dmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DMakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DMakerApplication.class, args);
		System.out.println("fight!"); // 달아놓는 것이 편하당
	}

}
