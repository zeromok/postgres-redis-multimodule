package com.daniel.practice.integration.config;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daniel.practice.integration.entity.Users;
import com.daniel.practice.integration.enums.UserStatus;
import com.daniel.practice.integration.repository.UserRepository;

@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner initUsers(UserRepository userRepository) {
		return args -> {
			if (userRepository.count() == 0) { // 중복 삽입 방지
				userRepository.save(Users.builder()
					.email("alice@example.com")
					.name("alice")
					.status(UserStatus.ACTIVE)
					.providerInfo(Map.of("provider", "NONE"))
					.build());
				userRepository.save(Users.builder()
					.email("bob@example.com")
					.name("bob")
					.status(UserStatus.INACTIVE)
					.providerInfo(Map.of("provider", "GOOGLE"))
					.build());
				userRepository.save(Users.builder()
					.email("carol@example.com")
					.name("carol")
					.status(UserStatus.ACTIVE)
					.providerInfo(Map.of("provider", "KAKAO"))
					.build());
				userRepository.save(Users.builder()
					.email("dave@example.com")
					.name("dave")
					.status(UserStatus.INACTIVE)
					.providerInfo(Map.of("provider", "NONE"))
					.build());
				userRepository.save(Users.builder()
					.email("eve@example.com")
					.name("eve")
					.status(UserStatus.ACTIVE)
					.providerInfo(Map.of("provider", "NAVER"))
					.build());
				userRepository.save(Users.builder()
					.email("frank@example.com")
					.name("frank")
					.status(UserStatus.ACTIVE)
					.providerInfo(Map.of("provider", "GOOGLE"))
					.build());
				userRepository.save(Users.builder()
					.email("grace@example.com")
					.name("grace")
					.status(UserStatus.INACTIVE)
					.providerInfo(Map.of("provider", "KAKAO"))
					.build());
				userRepository.save(Users.builder()
					.email("heidi@example.com")
					.name("heidi")
					.status(UserStatus.ACTIVE)
					.providerInfo(Map.of("provider", "NONE"))
					.build());
				userRepository.save(Users.builder()
					.email("ivan@example.com")
					.name("ivan")
					.status(UserStatus.INACTIVE)
					.providerInfo(Map.of("provider", "GOOGLE"))
					.build());
				userRepository.save(Users.builder()
					.email("judy@example.com")
					.name("judy")
					.status(UserStatus.ACTIVE)
					.providerInfo(Map.of("provider", "KAKAO"))
					.build());
			}
		};
	}
}
