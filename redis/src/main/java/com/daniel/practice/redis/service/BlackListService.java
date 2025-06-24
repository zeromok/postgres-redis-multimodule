package com.daniel.practice.redis.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListService {

	private final JwtService jwtService;
	private final RedisTemplate<String, Object> redisTemplate;

	// 블랙리스트 등록
	public void addToBlackList(String token) {
		System.out.println("=== 블랙리스트 등록 ===");

		Optional<Jws<Claims>> claimsJws = jwtService.parseToken(token);
		long exp;
		if (claimsJws.isPresent()) {
			exp = claimsJws.get().getPayload().getExpiration().getTime();
			// 블랙리스트의 TTL을 "남은 만료 시간"으로 설정
			// 토큰이 원래 만료되는 시점까지만 블랙리스트에 남겨두면 충분
			redisTemplate.opsForValue().set("blacklist:" + token, token, (exp - System.currentTimeMillis()) / 1000);
		}
	}


	// 블랙리스트 조회
	public boolean isBlackListed(String token) {
		System.out.println("=== 블랙리스트 조회 ===");
		return redisTemplate.hasKey("blacklist:" + token);
	}
}
