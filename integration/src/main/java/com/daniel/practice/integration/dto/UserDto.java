package com.daniel.practice.integration.dto;

import lombok.Data;

@Data
public class UserDto {

	private Long id;
	private String username;
	private String email;
	private String status;      // 예: ACTIVE, INACTIVE 등
	private String roles;       // 예: "USER,ADMIN"
	private String socialType;  // 예: "GOOGLE", "KAKAO" 등 (소셜 로그인 연동 시)
	private String profileImageUrl;

	// 필요시 createdAt, updatedAt 등 추가
}
