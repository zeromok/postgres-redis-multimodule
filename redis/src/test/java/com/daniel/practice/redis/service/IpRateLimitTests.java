package com.daniel.practice.redis.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IpRateLimitTests {

	@Autowired
	private IpRateLimitService ipRateLimitService;

	@Test
	@DisplayName("IP별 분당 요청 제한 실험")
	void minuteRateLimitTest() {
		String ip = "192.168.1.1";

		System.out.println("=== 분당 요청 제한 실험 (최대 3회) ===");

		// 초기화
		ipRateLimitService.resetRateLimit(ip, "minute");

		// 3회 요청 (모두 허용)
		for (int i = 1; i <= 3; i++) {
			boolean allowed = ipRateLimitService.isAllowedPerMinute(ip, 3);
			System.out.println("요청 " + i + ": " + (allowed ? "허용" : "차단"));
		}

		// 4번째 요청 (차단)
		boolean allowed = ipRateLimitService.isAllowedPerMinute(ip, 3);
		System.out.println("요청 4: " + (allowed ? "허용" : "차단"));

		// 현재 상태 확인
		int count = ipRateLimitService.getCurrentCount(ip, "minute");
		long ttl = ipRateLimitService.getTTL(ip, "minute");
		System.out.println("현재 카운트: " + count + ", TTL: " + ttl + "초");
	}

	@Test
	@DisplayName("IP별 시간당 요청 제한 실험")
	void hourRateLimitTest() {
		String ip = "10.0.0.1";

		System.out.println("=== 시간당 요청 제한 실험 (최대 5회) ===");

		// 초기화
		ipRateLimitService.resetRateLimit(ip, "hour");

		// 5회 요청 (모두 허용)
		for (int i = 1; i <= 5; i++) {
			boolean allowed = ipRateLimitService.isAllowedPerHour(ip, 5);
			System.out.println("요청 " + i + ": " + (allowed ? "허용" : "차단"));
		}

		// 6번째 요청 (차단)
		boolean allowed = ipRateLimitService.isAllowedPerHour(ip, 5);
		System.out.println("요청 6: " + (allowed ? "허용" : "차단"));
	}

	@Test
	@DisplayName("다중 시간 윈도우 실험")
	void multipleWindowTest() {
		String ip = "203.0.113.1";

		System.out.println("=== 다중 시간 윈도우 실험 ===");

		// 초기화
		ipRateLimitService.resetAllRateLimits(ip);

		// 동일한 IP로 여러 윈도우 테스트
		System.out.println("분당 제한 확인: " + ipRateLimitService.isAllowedPerMinute(ip, 10));
		System.out.println("시간당 제한 확인: " + ipRateLimitService.isAllowedPerHour(ip, 100));
		System.out.println("일일 제한 확인: " + ipRateLimitService.isAllowedPerDay(ip, 1000));

		// 각 윈도우별 상태 확인
		System.out.println("분당 카운트: " + ipRateLimitService.getCurrentCount(ip, "minute"));
		System.out.println("시간당 카운트: " + ipRateLimitService.getCurrentCount(ip, "hour"));
		System.out.println("일일 카운트: " + ipRateLimitService.getCurrentCount(ip, "day"));
	}
}
