package com.daniel.practice.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisBasicTests {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	// @Test
	// @DisplayName("RedisConfig 설정 전 테스트")
	// void testDefaultSerialization() {
	// 	redisTemplate.opsForValue().set("test", "hi");
	// 	Object result = redisTemplate.opsForValue().get("test");
	//
	// 	System.out.println("RedisTemplate 타입: " + redisTemplate.getClass());
	// 	System.out.println("가져온 값: " + result);
	// }

	@Test
	@DisplayName("String 타입 저장 - 토큰, 세션ID")
	void saveForString() {
		// given
		redisTemplate.opsForValue().set("string", "hello, world");

		// when
		Object result = redisTemplate.opsForValue().get("string");

		// then
		System.out.println("저장한 값: hello, world");
		System.out.println("가져온 값: " + result);
		System.out.println("같은가? " + "hello, world".equals(result));
	}

	@Test
	@DisplayName("Hash 타입 저장 - 사용자 프로필")
	void saveForHash() {
		// Given
		Map<String, Object> user = new HashMap<>();
		user.put("name", "test");
		user.put("age", 30);
		user.put("email", "test@gmail.com");
		redisTemplate.opsForHash().putAll("hash", user);

		// When
		Map<Object, Object> resultMap = redisTemplate.opsForHash().entries("hash");

		// Then
		System.out.println("저장한 값: " + user);
		System.out.println("가져온 값: " + resultMap);
	}

	@Test
	@DisplayName("List 타입 저장 - 알림 큐")
	void saveForList() {
		// Given
		List<String> list = new ArrayList<>();
		list.add("로그인 성공");
		list.add("새 메시지 도착");
		// LIFO: leftPush 사용
		redisTemplate.opsForList().leftPushAll("list", list);


		// When
		List<Object> resultList = redisTemplate.opsForList().range("list", 0, -1);

		// Then
		System.out.println("저장한 값: "+ list);
		System.out.println("가져온 값: " + resultList);
	}

	@Test
	@DisplayName("Set 타입 저장 - 권한 관리")
	void saveForSet() {
		// Given
		// Map 이나 List 처럼 Set 객체를 만들어 넘기지 못한다.
		// 개별 .add() 호출 / 가변인자 활용(현재) / 배열로 변환해서 넘기기
		Long addResult = redisTemplate.opsForSet().add("set", "admin", "user", "manager");

		// When
		Set<Object> resultSet = redisTemplate.opsForSet().members("set");

		// Then
		System.out.println("저장한 데이터 수: " + addResult);
		System.out.println("가져온 값: " + resultSet);
	}
}
