package com.daniel.practice.redis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.service.IpRateLimitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ip-rate-limit")
@RequiredArgsConstructor
public class IpRateLimitController {

	private final IpRateLimitService ipRateLimitService;

	// 분 단위 요청 제한 확인 (실습용 엔드포인트)
	@GetMapping("/minute/{ip}")
	public ResponseEntity<?> checkMinuteLimit(@PathVariable String ip) {
		boolean isAllowed = ipRateLimitService.isAllowedPerMinute(ip, 5); // 분당 5회 제한
		return ResponseEntity.ok().body(
			isAllowed ? "✅ 분당 요청 허용: " + ip : "❌ 분당 요청 제한 초과: " + ip
		);
	}

	// 시간 단위 요청 제한 확인
	@GetMapping("/hour/{ip}")
	public ResponseEntity<?> checkHourLimit(@PathVariable String ip) {
		boolean isAllowed = ipRateLimitService.isAllowedPerHour(ip, 100); // 시간당 100회 제한
		return ResponseEntity.ok().body(
			isAllowed ? "✅ 시간당 요청 허용: " + ip : "❌ 시간당 요청 제한 초과: " + ip
		);
	}

	// 일 단위 요청 제한 확인
	@GetMapping("/day/{ip}")
	public ResponseEntity<?> checkDayLimit(@PathVariable String ip) {
		boolean isAllowed = ipRateLimitService.isAllowedPerDay(ip, 1000); // 일일 1000회 제한
		return ResponseEntity.ok().body(
			isAllowed ? "✅ 일일 요청 허용: " + ip : "❌ 일일 요청 제한 초과: " + ip
		);
	}

	// 현재 요청 횟수 조회
	@GetMapping("/count/{ip}/{window}")
	public ResponseEntity<?> getCurrentCount(@PathVariable String ip, @PathVariable String window) {
		int count = ipRateLimitService.getCurrentCount(ip, window);
		long ttl = ipRateLimitService.getTTL(ip, window);
		return ResponseEntity.ok().body(
			"IP: " + ip + ", Window: " + window + ", Count: " + count + ", TTL: " + ttl + "초"
		);
	}

	// Rate Limit 초기화 (테스트용)
	@DeleteMapping("/reset/{ip}")
	public ResponseEntity<?> resetRateLimit(@PathVariable String ip) {
		ipRateLimitService.resetAllRateLimits(ip);
		return ResponseEntity.ok().body("Rate Limit 초기화 완료: " + ip);
	}
}
