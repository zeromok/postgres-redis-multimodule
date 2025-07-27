package com.daniel.practice.integration.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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


	@Cacheable(value = "Users", key = "#id", unless = "#result == null")
	public UserDto getUser(Long id) {
		log.info("DB에서 조회: {}", id);
		Users foundUser = userRepository.findById(id).orElse(null);

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

	// 즉시 동기화: DB 업데이트와 캐시 업데이트를 동시에 수행
	@CachePut(value = "Users", key = "#id")
	public UserDto updateUserImmediateSync(Long id, UserDto userDto) {
		log.info("즉시 동기화 - 사용자 업데이트 시작: {}", id);

		UserDto result;

		Users findUser = userRepository.findById(id).orElse(null);
		if (findUser != null) {
			// update
			log.info("기존 사용자 갱신: {}", id);
			findUser.setName(userDto.getName());
			findUser.setEmail(userDto.getEmail());
			findUser.setStatus(userDto.getStatus());
			findUser.setProviderInfo(userDto.getProviderInfo());

			Users saveUser = userRepository.save(findUser);
			result = convertToDto(saveUser);
			log.info("즉시 동기화 - 캐시와 DB 동시 업데이트 완료: {}", saveUser);
		} else {
			// insert
			log.info("해당 id에 일치하는 사용자 없음 -> 새 사용자 생성");

			Users newUser = Users.builder()
				.name(userDto.getName())
				.email(userDto.getEmail())
				.status(userDto.getStatus())
				.providerInfo(userDto.getProviderInfo())
				.build();

			Users saveUser = userRepository.save(newUser);
			result = convertToDto(saveUser);
			log.info("즉시 동기화 - 새 사용자 생성 및 캐시 저장 완료: {}", saveUser);
		}

		// @CachePut 이 이 결과를 캐시에 즉시 저장
		return result;
	}

	// 변환 헬퍼 메서드
	private UserDto convertToDto(Users user) {
		if (user == null) return null;

		return UserDto.builder()
			.id(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.status(user.getStatus())
			.providerInfo(user.getProviderInfo())
			.createdAt(user.getCreatedAt())
			.lastModifiedAt(user.getLastModifiedAt())
			.build();
	}

	// 즉시 동기화: 캐시 무효화 후 DB 업데이트
	@CacheEvict(value = "Users", key = "#id")
	public void updateUserWithCacheEvict(Long id, UserDto userDto) {
		log.info("캐시 무효화 후 DB 업데이트: {}", id);

		Users existingUser = userRepository.findById(id).orElse(null);
		if (existingUser != null) {
			existingUser.setName(userDto.getName());
			existingUser.setEmail(userDto.getEmail());
			existingUser.setStatus(userDto.getStatus());
			existingUser.setProviderInfo(userDto.getProviderInfo());

			userRepository.save(existingUser);
			log.info("캐시 무효화 완료, DB 업데이트 완료: {}", id);
		}
	}

	// 지연 동기화: 캐시만 먼저 업데이트하고 DB는 나중에 업데이트

	// 지연 동기화: 배치 처리로 DB 업데이트

	public void deleteUser(Long id) {

	}
}
