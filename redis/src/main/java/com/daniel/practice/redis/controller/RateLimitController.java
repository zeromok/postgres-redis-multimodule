package com.daniel.practice.redis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.dto.TokenUsageInfo;
import com.daniel.practice.redis.service.TokenUsageLimitService;
import com.daniel.practice.redis.service.UserRateLimitService;

import lombok.RequiredArgsConstructor;

@RestController("/rate-limit")
@RequiredArgsConstructor
public class RateLimitController {

	private final UserRateLimitService userRateLimitService;
	private final TokenUsageLimitService tokenUsageLimitService;

	// == 사용자별 요청 횟수 제한 ==
	// 사용자별 시간당 요청 제한 확인
	@GetMapping("/user/hour/{userId}")
	public ResponseEntity<?> checkUserHourlyLimit(@PathVariable String userId) {
		boolean isAllowed = userRateLimitService.isAllowedPerHour(userId);
		int currentCount = userRateLimitService.getCurrentHourlyCount(userId);
		int limit = getUserIdHourlyLimit(userId);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("isAllowed", isAllowed);
		response.put("currentCount", currentCount);
		response.put("limit", limit);
		response.put("message", isAllowed ?
			"✅ 시간당 요청 허용" :
			"❌ 시간당 요청 제한 초과");

		return ResponseEntity.ok(response);
	}

	// 사용자별 일일 요청 제한 확인
	@GetMapping("/user/day/{userId}")
	public ResponseEntity<?> checkUserDailyLimit(@PathVariable String userId) {
		boolean isAllowed = userRateLimitService.isAllowedPerDay(userId);
		int currentCount = userRateLimitService.getCurrentDailyCount(userId);
		int limit = getUserIdDailyLimit(userId);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("isAllowed", isAllowed);
		response.put("currentCount", currentCount);
		response.put("limit", limit);
		response.put("message", isAllowed ?
			"✅ 일일 요청 허용" :
			"❌ 일일 요청 제한 초과");

		return ResponseEntity.ok(response);
	}

	// 시간당 요청 횟수 조회
	@GetMapping("/user/hourlyCount/{userId}")
	public ResponseEntity<?> hourlyCountInquiry(@PathVariable String userId) {
		int hourlyCount = userRateLimitService.getCurrentHourlyCount(userId);
		int limit = getUserIdHourlyLimit(userId);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("hourlyCount", hourlyCount);
		response.put("limit", limit);
		response.put("remaining", Math.max(0, limit - hourlyCount));

		return ResponseEntity.ok(response);
	}

	// 일일 요청 횟수 조회
	@GetMapping("/user/dailyCount/{userId}")
	public ResponseEntity<?> dailyCountInquiry(@PathVariable String userId) {
		int dailyCount = userRateLimitService.getCurrentDailyCount(userId);
		int limit = getUserIdDailyLimit(userId);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("dailyCount", dailyCount);
		response.put("limit", limit);
		response.put("remaining", Math.max(0, limit - dailyCount));

		return ResponseEntity.ok(response);
	}


	// == 토큰별 요청 횟수 제한 ==
	// 토큰별 사용 횟수 제한 확인
	@GetMapping("/token/{token}")
	public ResponseEntity<?> checkTokenUsage(@PathVariable String token) {
		boolean isAllowed = tokenUsageLimitService.isAllowed(token);
		TokenUsageInfo info = tokenUsageLimitService.getTokenUsageInfo(token);

		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("isAllowed", isAllowed);
		response.put("currentCount", info.getCount());
		response.put("limit", info.getLimit());
		response.put("ttl", info.getTtl());
		response.put("remaining", Math.max(0, info.getLimit() - info.getCount()));
		response.put("message", isAllowed ?
			"✅ 토큰 사용 허용" :
			"❌ 토큰 사용 제한 초과");

		return ResponseEntity.ok(response);
	}

	// 토큰 사용 정보 상세 조회
	@GetMapping("/token/info/{token}")
	public ResponseEntity<?> getTokenInfo(@PathVariable String token) {
		TokenUsageInfo info = tokenUsageLimitService.getTokenUsageInfo(token);
		return ResponseEntity.ok(info);
	}

	// 토큰 사용 이력 조회
	@GetMapping("/token/history/{token}")
	public ResponseEntity<?> getTokenHistory(@PathVariable String token) {
		List<Long> history = tokenUsageLimitService.getUsageHistory(token);

		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("usageHistory", history);
		response.put("historyCount", history.size());

		return ResponseEntity.ok(response);
	}

	// == 통합 제한 확인 ==
	// 사용자 + 토큰 통합 제한 확인
	@GetMapping("/combined/{userId}/{token}")
	public ResponseEntity<?> checkCombinedLimit(@PathVariable String userId, @PathVariable String token) {
		boolean userHourlyAllowed = userRateLimitService.isAllowedPerHour(userId);
		boolean userDailyAllowed = userRateLimitService.isAllowedPerDay(userId);
		boolean tokenAllowed = tokenUsageLimitService.isAllowed(token);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("token", token);
		response.put("userHourlyAllowed", userHourlyAllowed);
		response.put("userDailyAllowed", userDailyAllowed);
		response.put("tokenAllowed", tokenAllowed);
		response.put("overallAllowed", userHourlyAllowed && userDailyAllowed && tokenAllowed);

		// 상세 정보
		Map<String, Object> details = new HashMap<>();
		details.put("userHourlyCount", userRateLimitService.getCurrentHourlyCount(userId));
		details.put("userDailyCount", userRateLimitService.getCurrentDailyCount(userId));
		details.put("tokenCount", tokenUsageLimitService.getCurrentUsage(token));
		response.put("details", details);

		return ResponseEntity.ok(response);
	}

	// == 시뮬레이션 엔드포인트 ==
	// API 호출 시뮬레이션
	@PostMapping("/simulate/api-call/{userId}/{token}")
	public ResponseEntity<?> simulateApiCall(@PathVariable String userId, @PathVariable String token) {
		boolean userHourlyAllowed = userRateLimitService.isAllowedPerHour(userId);
		boolean userDailyAllowed = userRateLimitService.isAllowedPerDay(userId);
		boolean tokenAllowed = tokenUsageLimitService.isAllowed(token);

		Map<String, Object> response = new HashMap<>();
		response.put("simulation", "API Call");
		response.put("userId", userId);
		response.put("token", token);
		response.put("success", userHourlyAllowed && userDailyAllowed && tokenAllowed);

		if (userHourlyAllowed && userDailyAllowed && tokenAllowed) {
			response.put("message", "✅ API 호출 성공");
		} else {
			response.put("message", "❌ API 호출 실패 - 제한 초과");
			response.put("reasons", new String[]{
				!userHourlyAllowed ? "시간당 사용자 제한" : null,
				!userDailyAllowed ? "일일 사용자 제한" : null,
				!tokenAllowed ? "토큰 사용 제한" : null
			});
		}

		return ResponseEntity.ok(response);
	}

	// 대량 요청 시뮬레이션
	@PostMapping("/simulate/bulk-requests/{userId}/{token}/{count}")
	public ResponseEntity<?> simulateBulkRequests(
		@PathVariable String userId,
		@PathVariable String token,
		@PathVariable int count) {

		Map<String, Object> response = new HashMap<>();
		response.put("simulation", "Bulk Requests");
		response.put("userId", userId);
		response.put("token", token);
		response.put("requestCount", count);

		int successCount = 0;
		int failureCount = 0;

		for (int i = 0; i < count; i++) {
			boolean userHourlyAllowed = userRateLimitService.isAllowedPerHour(userId);
			boolean userDailyAllowed = userRateLimitService.isAllowedPerDay(userId);
			boolean tokenAllowed = tokenUsageLimitService.isAllowed(token);

			if (userHourlyAllowed && userDailyAllowed && tokenAllowed) {
				successCount++;
			} else {
				failureCount++;
			}
		}

		response.put("successCount", successCount);
		response.put("failureCount", failureCount);
		response.put("successRate", String.format("%.2f%%", (double) successCount / count * 100));

		return ResponseEntity.ok(response);
	}

	// == 헬퍼 메서드 ==
	private int getUserIdHourlyLimit(String userId) {
		if (userId.startsWith("premium_")) {
			return 1000;
		} else if (userId.startsWith("enterprise_")) {
			return 10000;
		} else {
			return 100;
		}
	}

	private int getUserIdDailyLimit(String userId) {
		if (userId.startsWith("premium_")) {
			return 10000;
		} else if (userId.startsWith("enterprise_")) {
			return 100000;
		} else {
			return 1000;
		}
	}
}
