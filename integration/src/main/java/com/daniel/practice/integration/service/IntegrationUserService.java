package com.daniel.practice.integration.service;

import java.time.Duration;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.daniel.practice.integration.dto.UserDto;
import com.daniel.practice.integration.entity.Users;
import com.daniel.practice.integration.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationUserService {
	// Cache Aside 패턴
	// - 데이터 조회 시 먼저 Redis 캐시를 확인
	// - 캐시에 없으면(DB miss) DB 에서 조회 후 캐시에 저장
	// - 읽기 성능 향상, DB 부하 감소
	private final UserRepository userRepository;
	private final String CACHE_KEY = "Users:";


	@Cacheable(value = "Users", key = "#id", unless = "#result == null")
	public UserDto getUser(Long id) {
		log.info("DB에서 조회: {}", id);
		Users foundUser = userRepository.findUsersById(id);

		if (foundUser == null) {
			log.error("DB에서도 사용자를 찾을 수 없음: {}", id);
			return null;
		}

		log.info("DB 결과를 DTO로 변환");
		return UserDto.builder()
			.id(foundUser.getId())
			.name(foundUser.getName())
			.email(foundUser.getEmail())
			.status(foundUser.getStatus())
			.providerInfo(foundUser.getProviderInfo())
			.createdAt(foundUser.getCreatedAt())
			.lastModifiedAt(foundUser.getLastModifiedAt())
			.build();
	}

	public void updateUser(UserDto userDto) {

	}

	public void deleteUser(Long id) {

	}
}
