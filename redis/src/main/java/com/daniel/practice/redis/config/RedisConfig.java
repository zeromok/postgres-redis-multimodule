package com.daniel.practice.redis.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(jsonSerializer);

		// Hash
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(jsonSerializer);

		template.afterPropertiesSet(); // 모든 설정이 끝난 후 초기화
		return template;
	}

	// RedisTemplate과 RedisCacheManager는 직렬화 설정이 별개임.
	// RedisCacheManager 에서 value serializer를 명시적으로 지정하지 않으면,
	// 기본값(JdkSerializationRedisSerializer) 사용 → 즉, Serializable 필요.
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer())) // Serializable
			.disableCachingNullValues(); // null 값 캐싱 비활성화

		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(config)
			.build();
	}
}
