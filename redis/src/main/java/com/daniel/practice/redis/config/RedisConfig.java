package com.daniel.practice.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
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
}
