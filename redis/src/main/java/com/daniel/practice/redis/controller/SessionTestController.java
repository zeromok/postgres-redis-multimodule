package com.daniel.practice.redis.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionTestController {


	// 세션 저장
	@GetMapping("/set")
	public ResponseEntity<String> setSession(HttpSession session) {
		session.setAttribute("user_id", "user_01");
		session.setAttribute("provider", "google");

		return ResponseEntity.ok("세션 저장 완료: " + session.getId());
	}

	@GetMapping("/get")
	public ResponseEntity<Map<String, Object>> getSpringSession(HttpSession session) {
		Map<String, Object> data = new HashMap<>();
		data.put("session_id", session.getId());
		data.put("user_id", session.getAttribute("user_id"));
		data.put("provider", session.getAttribute("provider"));
		data.put("login_time", session.getAttribute("login_time"));

		return ResponseEntity.ok(data);
	}
}
