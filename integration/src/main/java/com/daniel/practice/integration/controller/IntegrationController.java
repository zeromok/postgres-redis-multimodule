package com.daniel.practice.integration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.integration.dto.UserDto;
import com.daniel.practice.integration.service.IntegrationUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IntegrationController {

	private final IntegrationUserService integrationUserService;

	// API 요청 -> Controller -> Service
	// service 에서 Redis 캐시 조회
	// 캐시에 없으면 DB(UserRepository)에서 조회
	// DB 결과를 Redis에 저장(TTL 설정)
	// 결과 반환
	@GetMapping("/{id}")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		UserDto user = integrationUserService.getUser(id);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.ok("사용자가 없습니다.");
	}

	// 유저 갱신 (캐시 지연/즉시 동기화)
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
		return null;
	}

	// 유저 삭제 (캐시 삭제)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		return null;
	}
}
