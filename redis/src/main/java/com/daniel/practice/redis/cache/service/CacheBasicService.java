package com.daniel.practice.redis.cache.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.daniel.practice.redis.cache.domain.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CacheBasicService {
	// 캐시 저장/조회/삭제/동기화 비즈니스 로직, Spring Cache 어노테이션 실습

	private final Map<Long, Product> db = new ConcurrentHashMap<>();

	// 메서드 결과를 캐시에 저장/조회
	@Cacheable(value = "product", key = "#id")
	public Product getProduct(long id) {
		log.info("DB 에서 조회: {}", id);

		return db.get(id);
	}

	// 캐시 갱신 (지연 동기화)
	// 변경 시 DB 에 먼저 저장 -> 동시에 캐시(Redis) 에서는 해당 상품 캐시 삭제
	// 다음 조회 시 DB 에서 읽고, 그 결과가 다시 캐시에 저장
	@CacheEvict(value = "product", key = "#id")
	public void lazyUpdateProduct(long id, Product product) {
		db.put(id, product);
		log.info("DB/캐시 동기화: {}", id);
	}

	// 캐시 갱신 (즉시 동기화)
	// 변경 시 DB 에 먼저 저장 -> 동시에 캐시(Redis) 도 갱신
	// 다음 조회 시 캐시에서 최신값 반환됨
	@CachePut(value = "product", key = "#id")
	public Product updateProduct(long id, Product product) {
		db.put(id, product);
		log.info("DB/캐시 동기화: {}", id);

		return product;
	}

	// 캐시 삭제 (무효화)
	@CacheEvict(value = "product", key = "#id")
	public void deleteProduct(long id) {
		db.remove(id);
		log.info("DB/캐시 삭제: {}", id);
	}

	// 테스트용 데이터 추가
	public void addProduct(Product product) {
		db.put(product.getId(), product);
	}
}
