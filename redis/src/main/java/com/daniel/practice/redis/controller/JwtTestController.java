package com.daniel.practice.redis.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.service.JwtService;
import com.daniel.practice.redis.service.RedisJwtService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/jwt-test")
@RequiredArgsConstructor
public class JwtTestController {
	private final JwtService jwtService;
	private final RedisJwtService redisJwtService;

	// 로그인 (토큰 생성 + Redis 저장)
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		System.out.println("=== 로그인 요청 ===");
		System.out.println("사용자 ID: " + request.getUserId());

		// 토큰 생성 (1시간 유효)
		String token = jwtService.generateToken(request.getUserId(), "USER", 3_600_000L);

		// Redis에 토큰 저장 (30분 TTL)
		redisJwtService.storeToken(request.getUserId(), token, 1_800L);

		// 토큰 정보 출력
		jwtService.printTokenInfo(token);

		return ResponseEntity.ok(Map.of(
			"message", "로그인 성공",
			"token", token,
			"userId", request.getUserId()
		));
	}

	// 토큰 검증
	@GetMapping("/validate/{userId}")
	public ResponseEntity<?> validateToken(@PathVariable String userId,
		@RequestHeader("Authorization") String authHeader) {
		System.out.println("=== 토큰 검증 요청 ===");

		String token = authHeader.replace("Bearer ", "");
		boolean isValid = redisJwtService.validateToken(userId, token);

		if (isValid) {
			return ResponseEntity.ok(Map.of(
				"message", "토큰이 유효합니다",
				"userId", userId
			));
		} else {
			return ResponseEntity.status(401).body(Map.of(
				"message", "토큰이 유효하지 않습니다",
				"userId", userId
			));
		}
	}

	// 토큰 정보 조회
	@GetMapping("/info/{userId}")
	public ResponseEntity<?> getTokenInfo(@PathVariable String userId) {
		System.out.println("=== 토큰 정보 조회 ===");

		Optional<String> token = redisJwtService.getToken(userId);
		Optional<Long> ttl = redisJwtService.getTokenTTL(userId);

		if (token.isPresent()) {
			jwtService.printTokenInfo(token.get());
			return ResponseEntity.ok(Map.of(
				"userId", userId,
				"token", token.get().substring(0, Math.min(50, token.get().length())) + "...",
				"ttl", ttl.orElse(-1L),
				"hasToken", true
			));
		} else {
			return ResponseEntity.ok(Map.of(
				"userId", userId,
				"hasToken", false,
				"message", "토큰이 존재하지 않습니다"
			));
		}
	}

	// 로그아웃 (토큰 삭제)
	@DeleteMapping("/logout/{userId}")
	public ResponseEntity<?> logout(@PathVariable String userId) {
		System.out.println("=== 로그아웃 요청 ===");

		redisJwtService.removeToken(userId);

		return ResponseEntity.ok(Map.of(
			"message", "로그아웃 완료",
			"userId", userId
		));
	}

	// TTL 실험용 - 짧은 TTL로 토큰 생성
	@PostMapping("/login-short-ttl")
	public ResponseEntity<?> loginWithShortTTL(@RequestBody LoginRequest request) {
		System.out.println("=== 짧은 TTL 로그인 요청 ===");

		String token = jwtService.generateToken(request.getUserId(), "USER", 3_600_000L);

		// 10초 TTL로 저장 (실험용)
		redisJwtService.storeToken(request.getUserId(), token, 10L);

		return ResponseEntity.ok(Map.of(
			"message", "짧은 TTL로 로그인 성공 (10초 후 만료)",
			"token", token,
			"userId", request.getUserId(),
			"ttl", 10L
		));
	}

	// 토큰 만료 확인
	@GetMapping("/expired/{userId}")
	public ResponseEntity<?> checkTokenExpired(@PathVariable String userId,
		@RequestHeader("Authorization") String authHeader) {
		System.out.println("=== 토큰 만료 확인 ===");

		String token = authHeader.replace("Bearer ", "");
		boolean isExpired = redisJwtService.isTokenExpired(userId, token);

		return ResponseEntity.ok(Map.of(
			"userId", userId,
			"isExpired", isExpired,
			"message", isExpired ? "토큰이 만료되었습니다" : "토큰이 유효합니다"
		));
	}

	@Getter
	public static class LoginRequest {
		private String userId;
	}

	// 다중 세션 관리
	@PostMapping("/login-multiple")
	public ResponseEntity<?> loginMultiple(@RequestBody LoginRequest request) {
		System.out.println("=== 다중 세션 로그인 ===");

		// 여러 토큰 생성 (다른 기기/브라우저 시뮬레이션)
		String token1 = jwtService.generateToken(request.getUserId(), "USER", 3_600_000L);
		String token2 = jwtService.generateToken(request.getUserId(), "USER", 3_600_000L);

		// Redis에 저장 (다른 키로)
		redisJwtService.storeToken(request.getUserId() + ":device1", token1, 1800L);
		redisJwtService.storeToken(request.getUserId() + ":device2", token2, 1800L);

		return ResponseEntity.ok(Map.of(
			"message", "다중 세션 로그인 성공",
			"device1_token", token1,
			"device2_token", token2,
			"userId", request.getUserId()
		));
	}

	@DeleteMapping("/logout-device/{userId}/{deviceId}")
	public ResponseEntity<?> logoutDevice(@PathVariable String userId,
		@PathVariable String deviceId) {
		System.out.println("=== 특정 기기 로그아웃 ===");

		redisJwtService.removeToken(userId + ":" + deviceId);

		return ResponseEntity.ok(Map.of(
			"message", "특정 기기 로그아웃 완료",
			"userId", userId,
			"deviceId", deviceId
		));
	}

	// 토큰 갱신
	@PostMapping("/refresh/{userId}")
	public ResponseEntity<?> refreshToken(@PathVariable String userId,
		@RequestHeader("Authorization") String authHeader) {
		System.out.println("=== 토큰 갱신 ===");

		String oldToken = authHeader.replace("Bearer ", "");

		// 기존 토큰 검증
		if (!redisJwtService.validateToken(userId, oldToken)) {
			return ResponseEntity.status(401).body(Map.of("message", "기존 토큰이 유효하지 않습니다"));
		}

		// 새 토큰 생성
		String newToken = jwtService.generateToken(userId, "USER", 3_600_000L);

		// Redis에 새 토큰 저장 (기존 토큰 교체)
		redisJwtService.storeToken(userId, newToken, 1_800L);

		return ResponseEntity.ok(Map.of(
			"message", "토큰 갱신 성공",
			"newToken", newToken,
			"userId", userId
		));
	}

	// 토큰 통계 조회
	@GetMapping("/stats/{userId}")
	public ResponseEntity<?> getTokenStats(@PathVariable String userId) {
		System.out.println("=== 토큰 통계 조회 ===");

		Optional<String> token = redisJwtService.getToken(userId);
		Optional<Long> ttl = redisJwtService.getTokenTTL(userId);

		Map<String, Object> stats = Map.of(
			"userId", userId,
			"hasActiveToken", token.isPresent(),
			"ttlSeconds", ttl.orElse(-1L),
			"isExpired", ttl.map(t -> t <= 0).orElse(true),
			"tokenPreview", token.map(t -> t.substring(0, Math.min(20, t.length())) + "...").orElse("N/A")
		);

		return ResponseEntity.ok(stats);
	}
}

