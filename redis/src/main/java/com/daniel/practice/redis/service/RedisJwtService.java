package com.daniel.practice.redis.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisJwtService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtService jwtService;

	// 키 생성 헬퍼 메서드
	private static String createKey(String userId) {
		return "jwt:" + userId;
	}

	// JWT를 Redis에 저장하는 메서드 (TTL 설정)
	public void storeToken(String userId, String token, long ttlSeconds) {
		String key = createKey(userId);
		redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
		System.out.println("토큰이 Redis에 저장되었습니다 - 사용자: " + userId + ", TTL: " + ttlSeconds + "초");
	}

	// Redis 에서 JWT 조회하는 메서드
	public Optional<String> getToken(String userId) {
		String key = createKey(userId);
		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			System.out.println("Redis에서 토큰을 찾을 수 없습니다 - 사용자: " + userId);
		} else {
			System.out.println("Redis에서 토큰을 조회했습니다 - 사용자: " + userId);
		}
		return Optional.ofNullable(value).map(Object::toString);
	}

	// JWT 삭제 메서드 (로그아웃)
	public void removeToken(String userId) {
		String key = createKey(userId);
		Boolean deleted = redisTemplate.delete(key);
		if (deleted) {
			System.out.println("토큰이 Redis에서 삭제되었습니다 - 사용자: " + userId);
		} else {
			System.out.println("삭제할 토큰이 Redis에 없습니다 - 사용자: " + userId);
		}
	}

	// JWT 삭제 메서드 (토큰 기반 삭제)
	public void removeTokenByToken(String token) {
		// 1. Redis에서 모든 userId에 대해 토큰을 저장하지 않았다면,
		//    토큰을 값으로 갖는 모든 키를 찾을 수 없으므로,
		//    일반적으로는 userId <-> token 매핑이 필요함.
		// 2. 여기서는 "jwt:{userId}" -> token 구조라고 가정.

		// 모든 userId를 알 수 없으므로, 실무에서는
		// 1) 토큰에서 userId를 추출하거나,
		// 2) 별도의 토큰-유저 매핑 테이블을 둡니다.

		// 예시: 토큰에서 userId 추출
		Optional<String> userIdOpt = jwtService.getUserIdFromToken(token);
		if (userIdOpt.isPresent()) {
			String key = "jwt:" + userIdOpt.get();
			Object value = redisTemplate.opsForValue().get(key);
			if (value != null && value.toString().equals(token)) {
				redisTemplate.delete(key);
				System.out.println("토큰 기반 삭제 완료: " + key);
			} else {
				System.out.println("해당 토큰이 Redis에 존재하지 않음");
			}
		} else {
			System.out.println("토큰에서 userId 추출 실패");
		}
	}

	// JWT 유효성 검증 메서드 (Redis + JWT 검증)
	public boolean validateToken(String userId, String token) {
		System.out.println("=== 토큰 유효성 검증 시작 ===");
		System.out.println("사용자 ID: " + userId);

		boolean isValid = getToken(userId) // Redis에서 토큰 조회
			.filter(storedToken -> {
				boolean matches = storedToken.equals(token);
				if (!matches) {
					System.out.println("❌ 토큰이 일치하지 않습니다");
					System.out.println("저장된 토큰: " + storedToken.substring(0, Math.min(20, storedToken.length())) + "...");
					System.out.println("전달된 토큰: " + token.substring(0, Math.min(20, token.length())) + "...");
				}
				return matches;
			})
			.map(storedToken -> {
				boolean jwtValid = jwtService.parseToken(token).isPresent();
				if (jwtValid) {
					System.out.println("✅ 토큰 검증 성공");
				} else {
					System.out.println("❌ JWT 토큰 검증 실패");
				}
				return jwtValid;
			})
			.orElse(false);

		if (!isValid) {
			System.out.println("❌ Redis에 토큰이 없습니다 (로그아웃됨)");
		}

		System.out.println("=== 토큰 유효성 검증 완료 ===");
		return isValid;
	}

	// TTL 조회 메서드
	public Optional<Long> getTokenTTL(String userId) {
		String key = createKey(userId);
		Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
		if (ttl > 0) {
			System.out.println("토큰 TTL: " + ttl + "초 남음 - 사용자: " + userId);
		} else {
			System.out.println("토큰이 만료되었거나 존재하지 않습니다 - 사용자: " + userId);
		}
		return Optional.of(ttl);
	}

	// 토큰 만료 확인 메서드
	public boolean isTokenExpired(String userId, String token) {
		// Redis TTL 확인
		Optional<Long> ttl = getTokenTTL(userId);
		if (ttl.isEmpty() || ttl.get() <= 0) {
			System.out.println("❌ Redis TTL 만료");
			return true;
		}

		// JWT 자체 만료 확인
		boolean jwtExpired = jwtService.isTokenExpired(token);
		if (jwtExpired) {
			System.out.println("❌ JWT 토큰 만료");
		}

		return jwtExpired;
	}
}
