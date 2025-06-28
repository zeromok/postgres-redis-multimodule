package com.daniel.practice.redis.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.service.IpRestrictionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ip-restriction")
@RequiredArgsConstructor
public class IpRestrictionController {

	private final IpRestrictionService ipRestrictionService;

	// IP 접근 허용 여부 확인 (실습용 엔드포인트)
	@GetMapping("/check/{ip}")
	public ResponseEntity<?> checkAccess(@PathVariable String ip) {
		boolean isAllowed = ipRestrictionService.isAllowed(ip);
		return ResponseEntity.ok().body(
			isAllowed ? "✅ 접근 허용: " + ip : "❌ 접근 차단: " + ip
		);
	}

	// 화이트리스트에 IP 추가
	@PostMapping("/whitelist/{ip}")
	public ResponseEntity<?> addToWhitelist(@PathVariable String ip) {
		ipRestrictionService.addToWhiteList(ip);
		return ResponseEntity.ok().body("화이트리스트에 추가됨: " + ip);
	}

	// 블랙리스트에 IP 추가
	@PostMapping("/blacklist/{ip}")
	public ResponseEntity<?> addToBlacklist(@PathVariable String ip) {
		ipRestrictionService.addToBlackList(ip);
		return ResponseEntity.ok().body("블랙리스트에 추가됨: " + ip);
	}

	// 화이트리스트에서 IP 제거
	@DeleteMapping("/whitelist/{ip}")
	public ResponseEntity<?> removeFromWhitelist(@PathVariable String ip) {
		ipRestrictionService.removeFromWhitelist(ip);
		return ResponseEntity.ok().body("화이트리스트에서 제거됨: " + ip);
	}

	// 블랙리스트에서 IP 제거
	@DeleteMapping("/blacklist/{ip}")
	public ResponseEntity<?> removeFromBlacklist(@PathVariable String ip) {
		ipRestrictionService.removeFromBlacklist(ip);
		return ResponseEntity.ok().body("블랙리스트에서 제거됨: " + ip);
	}

	// 화이트리스트 전체 조회
	@GetMapping("/whitelist")
	public ResponseEntity<?> getWhitelist() {
		Set<Object> whitelist = ipRestrictionService.getWhitelist();
		return ResponseEntity.ok().body("화이트리스트: " + whitelist);
	}

	// 블랙리스트 전체 조회
	@GetMapping("/blacklist")
	public ResponseEntity<?> getBlacklist() {
		Set<Object> blacklist = ipRestrictionService.getBlacklist();
		return ResponseEntity.ok().body("블랙리스트: " + blacklist);
	}
}
