package com.daniel.practice.integration.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.daniel.practice.integration.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;
	private String name;
	private String email;
	private UserStatus status;
	private Map<String, Object> providerInfo;

	// BaseTimeEntity에서 상속받는 필드들
	private LocalDateTime createdAt;
	private LocalDateTime lastModifiedAt;
}
