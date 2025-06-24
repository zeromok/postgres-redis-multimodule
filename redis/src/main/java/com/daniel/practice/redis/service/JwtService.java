package com.daniel.practice.redis.service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

	// SecretKey: 실제 운영 환경에서는 환경변수나 설정 파일에서 관리
	private final SecretKey key;

	public JwtService() {
		this.key = Jwts.SIG.HS256.key().build();;
	}

	// JWT 생성 메서드
	public String generateToken(String userId, String role, long expirationMs) {
		String token = Jwts.builder()
			.header()
				.type("JWT")
				.keyId("test-token")
				.and()
			.issuer("admin")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + expirationMs))
			.subject("test-jwt")
			.claims(Map.of("userId", userId, "role", role))
			.signWith(key)
			.compact();
		return token;
	}

	// JWT 검증 메서드
	public Optional<Jws<Claims>> parseToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return Optional.of(claims);
		} catch (JwtException | IllegalArgumentException e) {
			System.out.println("토큰 파싱 실패: " + e.getMessage());
			return Optional.empty();
		}
	}

	// JWT 에서 사용자 ID 추출 메서드
	public  Optional<String> getUserIdFromToken(String token) {
		return parseToken(token)
			.map(claims -> claims.getPayload().get("userId", String.class));
	}

	// JWT 토큰 만료 확인 메서드
	public boolean isTokenExpired(String token) {
		return parseToken(token)
			.map(claims -> {
				boolean expired = claims.getPayload().getExpiration().before(new Date());
				if (expired) {
					System.out.println("토큰이 만료되었습니다: " + claims.getPayload().getExpiration());
				}
				return expired;
			})
			.orElse(true); // 파싱 실패 시 만료된 것으로 처리
	}

	// 토큰 정보 출력 메서드 (디버깅용)
	public void printTokenInfo(String token) {
		parseToken(token).ifPresentOrElse(
			claims -> {
				System.out.println("=== 토큰 정보 ===");
				System.out.println("사용자 ID: " + claims.getPayload().get("userId"));
				System.out.println("역할: " + claims.getPayload().get("role"));
				System.out.println("발급 시간: " + claims.getPayload().getIssuedAt());
				System.out.println("만료 시간: " + claims.getPayload().getExpiration());
				System.out.println("현재 시간: " + new Date());
				System.out.println("만료 여부: " + claims.getPayload().getExpiration().before(new Date()));
				System.out.println("=================");
			},
			() -> System.out.println("토큰 정보 출력 중 오류: 유효하지 않은 토큰")
		);
	}

}
