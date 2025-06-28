package com.daniel.practice.redis.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IpRestrictionTests {

	@Autowired
	private IpRestrictionService ipRestrictionService;

	@Test
	@DisplayName("IP 화이트리스트/블랙리스트 기본 실험")
	void basicIpRestrictionTest() {
		// 1. 초기 상태 확인 (화이트리스트가 비어있으면 모든 IP 허용)
		System.out.println("=== 초기 상태 확인 ===");
		System.out.println("192.168.1.1 접근 허용: " + ipRestrictionService.isAllowed("192.168.1.1"));
		System.out.println("10.0.0.1 접근 허용: " + ipRestrictionService.isAllowed("10.0.0.1"));

		// 2. 화이트리스트에 IP 추가
		System.out.println("\n=== 화이트리스트에 IP 추가 ===");
		ipRestrictionService.addToWhiteList("192.168.1.1");
		ipRestrictionService.addToWhiteList("10.0.0.1");
		System.out.println("화이트리스트: " + ipRestrictionService.getWhitelist());

		// 3. 화이트리스트 기반 접근 확인
		System.out.println("\n=== 화이트리스트 기반 접근 확인 ===");
		System.out.println("192.168.1.1 접근 허용: " + ipRestrictionService.isAllowed("192.168.1.1"));
		System.out.println("10.0.0.1 접근 허용: " + ipRestrictionService.isAllowed("10.0.0.1"));
		System.out.println("203.0.113.1 접근 허용: " + ipRestrictionService.isAllowed("203.0.113.1"));

		// 4. 블랙리스트에 IP 추가
		System.out.println("\n=== 블랙리스트에 IP 추가 ===");
		ipRestrictionService.addToBlackList("203.0.113.1");
		System.out.println("블랙리스트: " + ipRestrictionService.getBlacklist());

		// 5. 블랙리스트 우선순위 확인
		System.out.println("\n=== 블랙리스트 우선순위 확인 ===");
		System.out.println("203.0.113.1 접근 허용: " + ipRestrictionService.isAllowed("203.0.113.1"));

		// 6. IP 제거 실험
		System.out.println("\n=== IP 제거 실험 ===");
		ipRestrictionService.removeFromWhitelist("192.168.1.1");
		ipRestrictionService.removeFromBlacklist("203.0.113.1");
		System.out.println("192.168.1.1 접근 허용: " + ipRestrictionService.isAllowed("192.168.1.1"));
		System.out.println("203.0.113.1 접근 허용: " + ipRestrictionService.isAllowed("203.0.113.1"));
	}
}
