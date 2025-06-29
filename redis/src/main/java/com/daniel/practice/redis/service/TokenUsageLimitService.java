package com.daniel.practice.redis.service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.daniel.practice.redis.dto.TokenUsageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenUsageLimitService {

	private final RedisTemplate<String, Object> redisTemplate;

	// 토큰 타입별 제한값
	private static final int PAYMENT_TOKEN_LIMIT = 10;
	private static final int API_TOKEN_LIMIT = 1000;
	private static final int TEMP_TOKEN_LIMIT = 1;

	// 키 생성 메서드
	private static String createTokenUsageKey(String token) {
		return "token:usage:" + token;
	}

	// 토큰별 사용 횟수 제한 확인
	public boolean isAllowed(String token) {
		String key = createTokenUsageKey(token);
		int tokenLimit = getTokenLimit(token);

		Object currCnt = redisTemplate.opsForHash().get(key, "count");
		int count = currCnt == null ? 0 : Integer.parseInt(currCnt.toString());

		System.out.println("토큰 사용 횟수 확인 - Token: " + token + ", 현재: " + count);

		if (count >= tokenLimit) {
			System.out.println("토큰 사용 횟수 초과");
			return false;
		}

		Long newCount = redisTemplate.opsForHash().increment(key, "count", 1);

		// 첫 번째 사용이면 추가 정보 설정
		if (newCount == 1) {
			// 제한값 설정
			redisTemplate.opsForHash().put(key, "limit", tokenLimit);
			// 첫 사용 시간 설정
			redisTemplate.opsForHash().put(key, "firstUsed", Instant.now().getEpochSecond());
			// TTL 설정 (1시간)
			redisTemplate.expire(key, 3600, TimeUnit.SECONDS);
			System.out.println("✅ 첫 번째 토큰 사용, Hash 정보 설정 및 TTL 설정");
		}

		// 마지막 사용 시간 업데이트
		redisTemplate.opsForHash().put(key, "lastUsed", Instant.now().getEpochSecond());

		System.out.println("✅ 토큰 사용 허용: " + newCount + "/" + tokenLimit);
		return true;
	}

	// 토큰 타입별 제한값 조회
	private int getTokenLimit(String token) {
		if (token.startsWith("pay_")) {
			return PAYMENT_TOKEN_LIMIT;
		} else if (token.startsWith("api_")) {
			return API_TOKEN_LIMIT;
		} else if (token.startsWith("temp_")) {
			return TEMP_TOKEN_LIMIT;
		} else {
			return API_TOKEN_LIMIT; // 기본값
		}
	}

	// 현재 사용 횟수 조회
	public int getCurrentUsage(String token) {
		String key = createTokenUsageKey(token);
		Object count = redisTemplate.opsForHash().get(key, "count");
		return count == null ? 0 : Integer.parseInt(count.toString());
	}

	// 토큰 제한값 조회
	public int getTokenLimitValue(String token) {
		String key = createTokenUsageKey(token);
		Object limit = redisTemplate.opsForHash().get(key, "limit");
		if (limit != null) {
			return Integer.parseInt(limit.toString());
		}
		return getTokenLimit(token); // Hash에 없으면 기본값 반환
	}

	// TTL 조회
	public long getTokenTTL(String token) {
		String key = createTokenUsageKey(token);
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	// 첫 사용 시간 조회
	public long getFirstUsedTime(String token) {
		String key = createTokenUsageKey(token);
		Object firstUsed = redisTemplate.opsForHash().get(key, "firstUsed");
		return firstUsed == null ? 0 : Long.parseLong(firstUsed.toString());
	}

	// 마지막 사용 시간 조회
	public long getLastUsedTime(String token) {
		String key = createTokenUsageKey(token);
		Object lastUsed = redisTemplate.opsForHash().get(key, "lastUsed");
		return lastUsed == null ? 0 : Long.parseLong(lastUsed.toString());
	}

	// 토큰 사용 정보 전체 조회
	public TokenUsageInfo getTokenUsageInfo(String token) {
		String key = createTokenUsageKey(token);

		Object count = redisTemplate.opsForHash().get(key, "count");
		Object limit = redisTemplate.opsForHash().get(key, "limit");
		Object firstUsed = redisTemplate.opsForHash().get(key, "firstUsed");
		Object lastUsed = redisTemplate.opsForHash().get(key, "lastUsed");

		return TokenUsageInfo.builder()
			.token(token)
			.count(count == null ? 0 : Integer.parseInt(count.toString()))
			.limit(limit == null ? getTokenLimit(token) : Integer.parseInt(limit.toString()))
			.firstUsed(firstUsed == null ? 0 : Long.parseLong(firstUsed.toString()))
			.lastUsed(lastUsed == null ? 0 : Long.parseLong(lastUsed.toString()))
			.ttl(getTokenTTL(token))
			.build();
	}

	// 토큰 사용 이력 추가 (선택사항)
	public void addUsageHistory(String token) {
		String key = createTokenUsageKey(token);
		String historyKey = "history";

		// List 구조로 사용 이력 저장
		redisTemplate.opsForList().rightPush(key + ":" + historyKey, Instant.now().getEpochSecond());

		// 이력은 최대 10개까지만 유지
		redisTemplate.opsForList().trim(key + ":" + historyKey, -10, -1);
	}

	// 토큰 사용 이력 조회
	public List<Long> getUsageHistory(String token) {
		String key = createTokenUsageKey(token);
		String historyKey = "history";

		List<Object> history = redisTemplate.opsForList().range(key + ":" + historyKey, 0, -1);
		return history.stream()
			.map(obj -> Long.parseLong(obj.toString()))
			.collect(Collectors.toList());
	}
}
