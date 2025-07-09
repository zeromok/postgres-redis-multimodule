package com.daniel.practice.integration.service;

import org.springframework.stereotype.Service;

import com.daniel.practice.integration.dto.UserDto;

@Service
public class IntegrationUserService {
	// Cache Aside 패턴
	// - 데이터 조회 시 먼저 Redis 캐시를 확인
	// - 캐시에 없으면(DB miss) DB 에서 조회 후 캐시에 저장
	// - 읽기 성능 향상, DB 부하 감소

	public UserDto getUser(Long id) {
		// 1. Redis 에서 조회

		// 2. 없으면 DB 에서 조회 후 Redis에 저장

		// 3. 결과 반환
		return null;
	}

	public void updateUser(UserDto userDto) {

	}

	public void deleteUser(Long id) {

	}
}
