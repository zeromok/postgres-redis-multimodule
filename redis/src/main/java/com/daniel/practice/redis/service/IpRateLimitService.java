package com.daniel.practice.redis.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpRateLimitService {

	private final RedisTemplate<String, Object> redisTemplate;

	// 키 생성 헬퍼 메서드
	private static String createRateLimitKey(String ip, String window) {
		return "ip:rate:" + window + ":" + ip;
	}

	// IP별 요청 횟수 제한 확인 (분 단위)
	public boolean isAllowedPerMinute(String ip, int maxRequests) {
		String key = createRateLimitKey(ip, "minute");
		return checkRateLimit(key, maxRequests, 60); // 60초 TTL
	}

	// IP별 요청 횟수 제한 확인 (시간 단위)
	public boolean isAllowedPerHour(String ip, int maxRequests) {
		String key = createRateLimitKey(ip, "hour");
		return checkRateLimit(key, maxRequests, 3600); // 3600초 TTL
	}

	// IP별 요청 횟수 제한 확인 (일 단위)
	public boolean isAllowedPerDay(String ip, int maxRequests) {
		String key = createRateLimitKey(ip, "day");
		return checkRateLimit(key, maxRequests, 86400); // 86400초 TTL
	}

	// Rate Limiting 로직
	private boolean checkRateLimit(String key, int maxRequests, long ttlSeconds) {
		// 현재 요청 횟수 조회
		Object currentCount = redisTemplate.opsForValue().get(key);
		int count = currentCount == null ? 0 : Integer.parseInt(currentCount.toString());

		System.out.println("IP 요청 횟수 확인 - Key: " + key + ", 현재: " + count + "/" + maxRequests);

		// 제한 초과 시 차단
		if (count >= maxRequests) {
			System.out.println("❌ Rate Limit 초과: " + key);
			return false;
		}

		// 요청 횟수 증가
		redisTemplate.opsForValue().increment(key);

		// 첫 번째 요청이면 TTL 설정
		if (count == 0) {
			redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
			System.out.println("✅ 첫 번째 요청, TTL 설정: " + ttlSeconds + "초");
		}

		System.out.println("✅ 요청 허용: " + (count + 1) + "/" + maxRequests);
		return true;
	}

	// 현재 요청 횟수 조회
	public int getCurrentCount(String ip, String window) {
		String key = createRateLimitKey(ip, window);
		Object count = redisTemplate.opsForValue().get(key);
		return count == null ? 0 : Integer.parseInt(count.toString());
	}

	// TTL 조회
	public long getTTL(String ip, String window) {
		String key = createRateLimitKey(ip, window);
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	// Rate Limit 초기화 (테스트용)
	public void resetRateLimit(String ip, String window) {
		String key = createRateLimitKey(ip, window);
		redisTemplate.delete(key);
		System.out.println("Rate Limit 초기화: " + key);
	}

	// 모든 Rate Limit 초기화 (테스트용)
	public void resetAllRateLimits(String ip) {
		resetRateLimit(ip, "minute");
		resetRateLimit(ip, "hour");
		resetRateLimit(ip, "day");
	}


}
