package com.daniel.practice.redis.cache.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.practice.redis.cache.domain.Product;
import com.daniel.practice.redis.cache.service.CacheBasicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheTestController {

	private final CacheBasicService cacheBasicService;

	@GetMapping("/{id}")
	public Product get(@PathVariable Long id) {
		return cacheBasicService.getProduct(id);
	}

	// 지연 동기화
	@PutMapping("/lazy/{id}")
	public void lazyUpdate(@PathVariable Long id, @RequestBody Product product) {
		cacheBasicService.lazyUpdateProduct(id, product);
	}

	// 즉시 동기화
	@PutMapping("/{id}")
	public void update(@PathVariable Long id, @RequestBody Product product) {
		cacheBasicService.updateProduct(id, product);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		cacheBasicService.deleteProduct(id);
	}

	@PostMapping("/add")
	public void add(@RequestBody Product product) {
		cacheBasicService.addProduct(product);
	}
}
