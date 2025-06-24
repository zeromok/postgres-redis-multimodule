package com.daniel.practice.redis.jwt;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@SpringBootTest
public class JwtTokenTests {

	String secret = "my-very-secret-key-which-is-long-enough";

	@Test
	@DisplayName("JWT 토큰 생성/파싱")
	void createAndParsingAndVerifyToken() {

		SecretKey key = Jwts.SIG.HS256.key().build();

		// JWT 토큰 생성
		String jwt = Jwts.builder()
			.header()
				.type("JWT")
				.keyId("myKeyId")
				.add("customHeader", "customValue")
				.and()
			.issuer("my-service")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + Duration.ofHours(2).toMillis()))
			.subject("subject")
			.claim("role", "USER")
			.signWith(key)
			.compact();
		System.out.println("토큰 생성 완료: " + jwt);

		// JWT 토큰 파싱/검증
		Jws<Claims> parsed = Jwts.parser()
			.verifyWith(key) // JWS 서명 검증용 키 명시
			.build()
			.parseSignedClaims(jwt); // 타입 안전한 파싱
		System.out.println("header: " + parsed.getHeader());
		System.out.println("issuer: " + parsed.getPayload().getIssuer());
		System.out.println("subject: " + parsed.getPayload().getSubject());
		System.out.println("role: " + parsed.getPayload().get("role"));

	}

	@Test
	@DisplayName("만료된 JWT 파싱 시 예외 발생")
	void expiredToken() {
		SecretKey key = Jwts.SIG.HS256.key().build();
		String jwt = Jwts.builder()
			.expiration(new Date(System.currentTimeMillis() - 1000)) // 이미 만료
			.signWith(key)
			.compact();

		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			System.out.println("만료 예외 발생: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("잘못된 키로 파싱 시 예외 발생")
	void wrongKey() {
		SecretKey key1 = Jwts.SIG.HS256.key().build();
		SecretKey key2 = Jwts.SIG.HS256.key().build(); // 다른 키
		String jwt = Jwts.builder()
			.signWith(key1)
			.compact();

		try {
			Jwts.parser().verifyWith(key2).build().parseSignedClaims(jwt);
		} catch (io.jsonwebtoken.security.SignatureException e) {
			System.out.println("서명 검증 실패: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("커스텀 헤더 값 추출")
	void customHeaderExtract() {
		SecretKey key = Jwts.SIG.HS256.key().build();
		String jwt = Jwts.builder()
			.header()
				.add("customHeader", "customValue")
				.and()
			.signWith(key)
			.claim("test", "test")
			.compact();

		Jws<Claims> parsed = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(jwt);

		String customHeader = (String) parsed.getHeader().get("customHeader");
		System.out.println("customHeader: " + customHeader);
	}

	@Test
	@DisplayName("subject 값 검증")
	void requireSubject() {
		SecretKey key = Jwts.SIG.HS256.key().build();
		String jwt = Jwts.builder()
			.subject("user123")
			.signWith(key)
			.compact();

		// 올바른 subject
		Jwts.parser()
			.verifyWith(key)
			.requireSubject("user123")
			.build()
			.parseSignedClaims(jwt);

		// 잘못된 subject
		try {
			Jwts.parser()
				.verifyWith(key)
				.requireSubject("other")
				.build()
				.parseSignedClaims(jwt);
		} catch (io.jsonwebtoken.IncorrectClaimException e) {
			System.out.println("subject 불일치: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("JWE 암호화/복호화 실험")
	void jweEncryptDecrypt() {
		SecretKey key = Jwts.ENC.A256GCM.key().build();
		String jwt = Jwts.builder()
			.subject("user123")
			.encryptWith(key, Jwts.ENC.A256GCM)
			.compact();

		String subject = Jwts.parser()
			.decryptWith(key)
			.build()
			.parseEncryptedClaims(jwt)
			.getPayload()
			.getSubject();
		System.out.println("복호화된 subject: " + subject);
	}
}
