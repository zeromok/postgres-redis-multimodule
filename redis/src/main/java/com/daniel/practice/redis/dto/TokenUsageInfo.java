package com.daniel.practice.redis.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenUsageInfo {
	private String token;
	private int count;
	private int limit;
	private long firstUsed;
	private long lastUsed;
	private long ttl;
}
