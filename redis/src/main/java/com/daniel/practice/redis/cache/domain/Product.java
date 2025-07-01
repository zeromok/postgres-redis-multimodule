package com.daniel.practice.redis.cache.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	// 캐시 대상 도메인 객체

	private long id;
	private String name;
	private int price;
}
