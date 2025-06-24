package com.daniel.practice.redis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.service.BlackListService;
import com.daniel.practice.redis.service.RedisJwtService;

import lombok.RequiredArgsConstructor;

@RestController("/blacklist")
@RequiredArgsConstructor
public class BlackListController {

	private final BlackListService blackListService;
	private final RedisJwtService redisJwtService;

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
		System.out.println("=== 블랙리스트 등록 ===");
		// 1. 토큰 추출
		String oldToken = authHeader.replace("Bearer ", "");

		// 2. 블랙리스트 등록 (TTL은 남은 만료 시간)
		blackListService.addToBlackList(oldToken);

		// 3. 기존 Redis 토큰 삭제
		redisJwtService.removeTokenByToken(oldToken);

		// 4. 응답
		return ResponseEntity.ok().body("블랙리스트 등록 및 로그아웃 완료");
	}

	// 인증 시 블랙리스트 체크 (실습용 엔드포인트)
	@GetMapping("/check")
	public ResponseEntity<?> check(@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.replace("Bearer ", "");
		boolean isBlack = blackListService.isBlackListed(token);
		return ResponseEntity.ok().body(isBlack ? "블랙리스트 토큰" : "정상 토큰");
	}
}
