package com.daniel.practice.redis.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRateLimitService {

	private final RedisTemplate<String, Object> redisTemplate;


	// 키 생성 헬퍼 메서드
	private static String createUserRateKey(String userId) {
		return "user:rate:" + userId;
	}

	// 사용자별 시간당 요청 제한 확인
	public boolean isAllowedPerHour(String userId) {
		String key = createUserRateKey(userId);
		int hourlyLimit = getUserHourlyLimit(userId);

		// Hash에서 hourly 카운터 조회
		Object currentCount = redisTemplate.opsForHash().get(key, "hourly");
		int count = currentCount == null ? 0 : Integer.parseInt(currentCount.toString());

		System.out.println("사용자 시간당 요청 횟수: " + count + "/" + hourlyLimit);

		if (count >= hourlyLimit) {
			return false;
		}

		// Hash에서 hourly 카운터 증가
		Long newCount = redisTemplate.opsForHash().increment(key, "hourly", 1);

		// 첫 번째 요청이면 TTL 설정
		if (newCount == 1) {
			redisTemplate.expire(key, 3600, TimeUnit.SECONDS); // 1시간
		}

		return true;
	}

	// 사용자별 일일 요청 제한 확인
	public boolean isAllowedPerDay(String userId) {
		String key = createUserRateKey(userId);
		int dailyLimit = getUserDailyLimit(userId);

		// Hash에서 daily 카운터 조회
		Object currentCount = redisTemplate.opsForHash().get(key, "daily");
		int count = currentCount == null ? 0 : Integer.parseInt(currentCount.toString());

		if (count >= dailyLimit) {
			return false;
		}

		// Hash에서 daily 카운터 증가
		Long newCount = redisTemplate.opsForHash().increment(key, "daily", 1);

		// 첫 번째 요청이면 TTL 설정
		if (newCount == 1) {
			redisTemplate.expire(key, 86400, TimeUnit.SECONDS); // 24시간
		}

		return true;
	}

	// 시간당 요청 횟수 조회
	public int getCurrentHourlyCount(String userId) {
		String key = createUserRateKey(userId);
		Object count = redisTemplate.opsForHash().get(key, "hourly");
		return count == null ? 0 : Integer.parseInt(count.toString());
	}

	// 일별 요청 횟수 조회
	public int getCurrentDailyCount(String userId) {
		String key = createUserRateKey(userId);
		Object count = redisTemplate.opsForHash().get(key, "daily");
		return count == null ? 0 : Integer.parseInt(count.toString());
	}

	// 사용자 등급별 제한값 조회
	private int getUserHourlyLimit(String userId) {
		if (userId.startsWith("premium_")) {
			return 1000;
		} else if (userId.startsWith("enterprise_")) {
			return 10000;
		} else {
			return 100;
		}
	}

	private int getUserDailyLimit(String userId) {
		if (userId.startsWith("premium_")) {
			return 10000;
		} else if (userId.startsWith("enterprise_")) {
			return 100000;
		} else {
			return 1000;
		}
	}
}
