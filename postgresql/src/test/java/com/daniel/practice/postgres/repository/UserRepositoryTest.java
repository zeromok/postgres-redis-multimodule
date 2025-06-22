package com.daniel.practice.postgres.repository;

import com.daniel.practice.postgres.entity.Users;
import com.daniel.practice.postgres.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * UserRepositoryTest - Docker Compose PostgreSQL 환경에서 테스트
 * 
 * 사전 조건: docker-compose up -d postgres 실행 필요
 * PostgreSQL 컨테이너가 localhost:5432에서 실행 중이어야 함
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Docker Compose PostgreSQL 연결 확인")
    void testDatabaseConnection() {
        // given & when: 사용자 저장
        Users user = createTestUser("connection@example.com", "Connection Test User");
        Users savedUser = userRepository.save(user);
        
        // then: PostgreSQL 연결 및 JSONB 저장 확인
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getProviderInfo()).isNotNull();
        assertThat(savedUser.getProviderInfo().get("provider")).isEqualTo("TEST");
    }

    @Test
    @DisplayName("사용자 저장 및 조회 - PostgreSQL 환경")
    void saveAndFindUser() {
        // given
        Users user = createTestUser("test@example.com", "Test User");

        // when
        Users savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(savedUser.getProviderInfo()).isNotNull();
        assertThat(savedUser.getProviderInfo().get("provider")).isEqualTo("TEST");
    }

    @Test
    @DisplayName("이메일로 사용자 검색 - PostgreSQL 환경")
    void findByEmail() {
        // given
        Users user = createTestUser("john@example.com", "John Doe");
        testEntityManager.persistAndFlush(user);

        // when
        Optional<Users> foundUser = userRepository.findByEmail("john@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("사용자 상태별 검색 - PostgreSQL 환경")
    void findByStatus() {
        // given
        Users activeUser = createTestUser("active@example.com", "Active User");
        Users inactiveUser = createTestUserWithStatus("inactive@example.com", "Inactive User", UserStatus.INACTIVE);

        testEntityManager.persistAndFlush(activeUser);
        testEntityManager.persistAndFlush(inactiveUser);

        // when
        List<Users> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);

        // then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("active@example.com");
    }

    @Test
    @DisplayName("이메일 중복 체크 테스트")
    void existsByEmail() {
        // given
        Users user = createTestUser("duplicate@example.com", "Test User");
        testEntityManager.persistAndFlush(user);

        // when & then
        assertThat(userRepository.existsByEmail("duplicate@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("notexist@example.com")).isFalse();
    }

    @Test
    @DisplayName("상태별 사용자 수 조회 테스트")
    void countByStatus() {
        // given
        for (int i = 1; i <= 3; i++) {
            Users user = createTestUser("user" + i + "@example.com", "User " + i);
            testEntityManager.persistAndFlush(user);
        }

        // when
        long activeCount = userRepository.countByStatus(UserStatus.ACTIVE);

        // then
        assertThat(activeCount).isEqualTo(3);
    }

    /**
     * 테스트용 사용자 생성 헬퍼 메서드
     */
    private Users createTestUser(String email, String name) {
        return createTestUserWithStatus(email, name, UserStatus.ACTIVE);
    }

    private Users createTestUserWithStatus(String email, String name, UserStatus status) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "TEST");
        providerInfo.put("providerId", "test_" + email.split("@")[0]);

        return Users.builder()
                .email(email)
                .name(name)
                .status(status)
                .providerInfo(providerInfo)
                .build();
    }
}
