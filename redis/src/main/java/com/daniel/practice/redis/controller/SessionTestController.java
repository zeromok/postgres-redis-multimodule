package com.daniel.practice.redis.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.service.RedisJwtService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionTestController {

	private final RedisJwtService redisJwtService;

	// === 세션 기초 ===

	// 세션 저장
	@GetMapping("/set")
	public ResponseEntity<String> setSession(HttpSession session) {
		session.setAttribute("user_id", "user_01");
		session.setAttribute("provider", "google");

		return ResponseEntity.ok("세션 저장 완료: " + session.getId());
	}

	// 세션 조회
	@GetMapping("/get")
	public ResponseEntity<Map<String, Object>> getSpringSession(HttpSession session) {
		Map<String, Object> data = new HashMap<>();
		data.put("session_id", session.getId());
		data.put("user_id", session.getAttribute("user_id"));
		data.put("provider", session.getAttribute("provider"));
		data.put("login_time", session.getAttribute("login_time"));

		return ResponseEntity.ok(data);
	}

	// === 세션 기반 인증 ===

	// 세션 로그인 (세션 생성&저장)
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> sessionLogin(HttpSession session, @RequestParam String userId) {
		session.setAttribute("user_id", userId);
		session.setAttribute("login_time", System.currentTimeMillis());

		Map<String, Object> response = new HashMap<>();
		response.put("type", "session");
		response.put("session_id", session.getId());
		response.put("user_id", userId);
		response.put("message", "세션 로그인 완료");

		log.info("=== 세션 로그인 ===");
		log.info("로그인 ID: {} - 세션 ID: {}", userId, session.getId());

		return ResponseEntity.ok(response);
	}

	// 세션 조회 (로그인 정보 조회)
	@GetMapping("/info")
	public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
		Map<String, Object> data = Map.of(
			"type", "session",
			"session_id", session.getId(),
			"user_id", session.getAttribute("user_id"),
			"is_valid", session.getAttribute("user_id") != null
		);

		log.info("=== 세션 조회 ===");
		log.info("로그인 ID: {} - 세션 ID: {}", session.getAttribute("user_id"), session.getId());

		return ResponseEntity.ok(data);
	}

	// 세션 로그아웃 (세션 무효화)
	@DeleteMapping("/logout")
	public ResponseEntity<Map<String, Object>> sessionLogout(HttpSession session) {
		String sessionId = session.getId();
		session.invalidate(); // 세션 무효화

		Map<String, Object> response = new HashMap<>();
		response.put("type", "session");
		response.put("session_id", sessionId);
		response.put("message", "세션 로그아웃 완료");

		log.info("=== 세션 로그아웃 ===");

		return ResponseEntity.ok(response);
	}

	@GetMapping("/compare")
	public ResponseEntity<Map<String, Object>> compareSessionJwt(HttpSession session) {
		// 세션 정보
		String sessionUserId = (String) session.getAttribute("user_id");
		boolean sessionValid = sessionUserId != null;

		// JWT 정보 (예시: user123)
		String jwtUserId = "user123";
		String jwtToken = redisJwtService.getToken(jwtUserId).orElse(null);
		boolean jwtValid = jwtToken != null && redisJwtService.validateToken(jwtUserId, jwtToken);

		Map<String, Object> comparison = new HashMap<>();
		comparison.put("session", Map.of(
			"user_id", sessionUserId,
			"session_id", session.getId(),
			"is_valid", sessionValid
		));
		comparison.put("jwt", Map.of(
			"user_id", jwtUserId,
			"has_token", jwtToken != null,
			"is_valid", jwtValid
		));

		log.info("=== 세션 vs JWT 비교 ===");
		log.info("세션 유효: " + sessionValid);
		log.info("JWT 유효: " + jwtValid);

		return ResponseEntity.ok(comparison);
	}
}
