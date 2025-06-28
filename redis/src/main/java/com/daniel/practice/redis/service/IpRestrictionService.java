package com.daniel.practice.redis.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpRestrictionService {

	private static final String WHITELIST_KEY = "ip:whitelist";
	private static final String BLACKLIST_KEY = "ip:blacklist";
	private final RedisTemplate<String, Object> redisTemplate;


	// 키 생성 메서드

	// 화이트리스트 IP에 추가
	public void addToWhiteList(String ip) {
		redisTemplate.opsForSet().add(WHITELIST_KEY, ip);
		System.out.println("화이트 리스트에 IP 추가: " + ip);
	}

	// 블랙리스트 IP에 추가
	public void addToBlackList(String ip) {
		redisTemplate.opsForSet().add(BLACKLIST_KEY, ip);
		System.out.println("블랙 리스트에 IP 추가: " + ip);
	}

	// 화이트리스트에서 IP 제거
	public void removeFromWhitelist(String ip) {
		redisTemplate.opsForSet().remove(WHITELIST_KEY, ip);
		System.out.println("화이트리스트에서 IP 제거: " + ip);
	}

	// 블랙리스트에서 IP 제거
	public void removeFromBlacklist(String ip) {
		redisTemplate.opsForSet().remove(BLACKLIST_KEY, ip);
		System.out.println("블랙리스트에서 IP 제거: " + ip);
	}

	// IP 접근 허용 여부 확인
	public boolean isAllowed(String ip) {
		// 블랙리스트에 있으면 차단
		if (isBlacklisted(ip)) {
			System.out.println("❌ IP가 블랙리스트에 있음: " + ip);
			return false;
		}

		// 화이트리스트가 비어있으면 모든 IP 허용
		if (getWhitelistSize() == 0) {
			System.out.println("✅ 화이트리스트가 비어있어 모든 IP 허용: " + ip);
			return true;
		}

		// 화이트리스트에 있으면 허용
		boolean isWhitelisted = isWhitelisted(ip);
		if (isWhitelisted) {
			System.out.println("✅ IP가 화이트리스트에 있음: " + ip);
		} else {
			System.out.println("❌ IP가 화이트리스트에 없음: " + ip);
		}
		return isWhitelisted;
	}

	// 화이트리스트에 있는지 확인
	public boolean isWhitelisted(String ip) {
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(WHITELIST_KEY, ip));
	}

	// 블랙리스트에 있는지 확인
	public boolean isBlacklisted(String ip) {
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLACKLIST_KEY, ip));
	}

	// 화이트리스트 전체 조회
	public Set<Object> getWhitelist() {
		return redisTemplate.opsForSet().members(WHITELIST_KEY);
	}

	// 블랙리스트 전체 조회
	public Set<Object> getBlacklist() {
		return redisTemplate.opsForSet().members(BLACKLIST_KEY);
	}

	// 화이트리스트 크기
	public long getWhitelistSize() {
		return redisTemplate.opsForSet().size(WHITELIST_KEY);
	}

	// 블랙리스트 크기
	public long getBlacklistSize() {
		return redisTemplate.opsForSet().size(BLACKLIST_KEY);
	}
}
