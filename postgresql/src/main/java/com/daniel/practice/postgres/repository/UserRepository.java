package com.daniel.practice.postgres.repository;

import com.daniel.practice.postgres.entity.Users;
import com.daniel.practice.postgres.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL JSON 연산자를 활용한 Users 리포지토리
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // ================================
    // 기본 CRUD 메서드들
    // ================================
    
    /**
     * 이메일로 사용자 검색
     */
    Optional<Users> findByEmail(String email);
    
    /**
     * 상태별 사용자 검색
     */
    List<Users> findByStatus(UserStatus status);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 상태별 사용자 수 조회
     */
    long countByStatus(UserStatus status);

    // ================================
    // PostgreSQL JSON 연산자들 (모두 nativeQuery 사용)
    // ================================

    /**
     * JSON 필드 추출 연산자 (->>): OAuth 프로바이더별 사용자 검색
     */
    @Query(value = "SELECT * FROM users WHERE provider_info ->> 'provider' = :provider", nativeQuery = true)
    List<Users> findByProvider(@Param("provider") String provider);

    /**
     * JSON 포함 관계 연산자 (@>): 특정 속성을 포함하는 사용자 검색
     */
    @Query(value = "SELECT * FROM users WHERE provider_info @> CAST(:criteria AS jsonb)", nativeQuery = true)
    List<Users> findByProviderInfoContaining(@Param("criteria") String criteria);

    /**
     * JSON 키 존재 여부 연산자 (jsonb_exists): 특정 키를 가진 사용자 검색
     * ? 연산자 대신 jsonb_exists 함수 사용 (JPA 파라미터 바인딩 충돌 회피)
     */
    @Query(value = "SELECT * FROM users WHERE jsonb_exists(provider_info, :key)", nativeQuery = true)
    List<Users> findByProviderInfoHasKey(@Param("key") String key);

    /**
     * 복합 JSON 쿼리: 프로바이더와 이메일 인증 상태 동시 확인
     */
    @Query(value = "SELECT * FROM users WHERE provider_info ->> 'provider' = :provider AND provider_info ->> 'email_verified' = 'true'", nativeQuery = true)
    List<Users> findVerifiedUsersByProvider(@Param("provider") String provider);

    /**
     * JSON 집계 쿼리: 프로바이더별 사용자 수 통계
     */
    @Query(value = "SELECT provider_info ->> 'provider' AS provider, COUNT(*) AS user_count " +
                   "FROM users " +
                   "GROUP BY provider_info ->> 'provider' " +
                   "ORDER BY user_count DESC", nativeQuery = true)
    List<Object[]> countUsersByProvider();
}
