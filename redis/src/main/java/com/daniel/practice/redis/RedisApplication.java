package com.daniel.practice.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.SessionRepository;

@SpringBootApplication
@EnableCaching
public class RedisApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisApplication.class, args);

        // 이 코드를 임시로 추가해보세요
        System.out.println("=== Spring Session 관련 빈 확인 ===");

        try {
            SessionRepository sessionRepo = context.getBean(SessionRepository.class);
            System.out.println("✅ SessionRepository: " + sessionRepo.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("❌ SessionRepository 빈 없음");
        }

        try {
            RedisTemplate redisTemplate = context.getBean("sessionRedisTemplate", RedisTemplate.class);
            System.out.println("✅ SessionRedisTemplate: " + redisTemplate.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("❌ SessionRedisTemplate 빈 없음 (기본 RedisTemplate 사용)");
        }

        System.out.println("=================================");
    }
}
