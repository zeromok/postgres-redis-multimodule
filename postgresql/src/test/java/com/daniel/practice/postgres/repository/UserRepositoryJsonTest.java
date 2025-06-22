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

import static org.assertj.core.api.Assertions.*;

/**
 * PostgreSQL JSON 기능 전용 테스트
 * 
 * 실제 OAuth 시나리오를 기반으로 JSON 연산자들을 테스트
 * Docker Compose로 실행된 PostgreSQL 인스턴스 사용
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryJsonTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("JSON 연산자 (->>): OAuth 프로바이더별 사용자 검색")
    void testFindByProvider() {
        // given: 다양한 OAuth 프로바이더 사용자들 저장
        Users googleUser = createGoogleUser("google@example.com", "Google User");
        Users githubUser = createGitHubUser("github@example.com", "GitHub User"); 
        Users facebookUser = createFacebookUser("facebook@example.com", "Facebook User");

        testEntityManager.persistAndFlush(googleUser);
        testEntityManager.persistAndFlush(githubUser);
        testEntityManager.persistAndFlush(facebookUser);

        // when: GOOGLE 프로바이더 사용자들 검색
        List<Users> googleUsers = userRepository.findByProvider("GOOGLE");

        // then: GOOGLE 프로바이더 사용자만 조회되는지 검증
        assertThat(googleUsers).hasSize(1);
        assertThat(googleUsers.get(0).getEmail()).isEqualTo("google@example.com");
        assertThat(googleUsers.get(0).getProviderInfo().get("provider")).isEqualTo("GOOGLE");
    }

    @Test
    @DisplayName("JSON 포함 관계 검색 (@>): 특정 속성을 가진 사용자 검색")
    void testFindByProviderInfoContaining() {
        // given: 한국어 locale을 가진 사용자와 영어 locale을 가진 사용자 저장
        Users koreanUser = createUserWithLocale("korean@example.com", "Korean User", "ko_KR");
        Users englishUser = createUserWithLocale("english@example.com", "English User", "en_US");

        testEntityManager.persistAndFlush(koreanUser);
        testEntityManager.persistAndFlush(englishUser);

        // when: 한국어 locale을 가진 사용자 검색
        String criteria = "{\"locale\": \"ko_KR\"}";
        List<Users> koreanUsers = userRepository.findByProviderInfoContaining(criteria);

        // then: 한국어 locale 사용자만 조회되는지 검증
        assertThat(koreanUsers).hasSize(1);
        assertThat(koreanUsers.get(0).getEmail()).isEqualTo("korean@example.com");
        assertThat(koreanUsers.get(0).getProviderInfo().get("locale")).isEqualTo("ko_KR");
    }

    @Test
    @DisplayName("JSON 키 존재 여부 검색 (?): 특정 키를 가진 사용자 검색")
    void testFindByProviderInfoHasKey() {
        // given: email_verified 키를 가진 사용자와 없는 사용자 저장
        Users verifiedUser = createVerifiedUser("verified@example.com", "Verified User");
        Users unverifiedUser = createUnverifiedUser("unverified@example.com", "Unverified User");

        testEntityManager.persistAndFlush(verifiedUser);
        testEntityManager.persistAndFlush(unverifiedUser);

        // when: email_verified 키를 가진 사용자들 검색
        List<Users> usersWithEmailVerified = userRepository.findByProviderInfoHasKey("email_verified");

        // then: email_verified 키를 가진 사용자만 조회되는지 검증
        assertThat(usersWithEmailVerified).hasSize(1);
        assertThat(usersWithEmailVerified.get(0).getEmail()).isEqualTo("verified@example.com");
    }

    @Test
    @DisplayName("복합 JSON 쿼리: 프로바이더와 이메일 인증 상태 동시 확인")
    void testFindVerifiedUsersByProvider() {
        // given: GOOGLE 프로바이더 사용자들 (인증된 사용자와 미인증 사용자)
        Users verifiedGoogle = createVerifiedGoogleUser("verified.google@example.com", "Verified Google User");
        Users unverifiedGoogle = createUnverifiedGoogleUser("unverified.google@example.com", "Unverified Google User");

        testEntityManager.persistAndFlush(verifiedGoogle);
        testEntityManager.persistAndFlush(unverifiedGoogle);

        // when: GOOGLE 프로바이더 중 이메일 인증된 사용자만 검색
        List<Users> verifiedGoogleUsers = userRepository.findVerifiedUsersByProvider("GOOGLE");

        // then: 인증된 GOOGLE 사용자만 조회되는지 검증
        assertThat(verifiedGoogleUsers).hasSize(1);
        assertThat(verifiedGoogleUsers.get(0).getEmail()).isEqualTo("verified.google@example.com");
        assertThat(verifiedGoogleUsers.get(0).getProviderInfo().get("provider")).isEqualTo("GOOGLE");
        assertThat(verifiedGoogleUsers.get(0).getProviderInfo().get("email_verified")).isEqualTo(true);
    }

    // ================================
    // 테스트 데이터 생성 헬퍼 메서드들
    // ================================

    private Users createGoogleUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        providerInfo.put("locale", "ko_KR");
        providerInfo.put("picture", "https://lh3.googleusercontent.com/...");
        providerInfo.put("email_verified", true);

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createGitHubUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GITHUB");
        providerInfo.put("providerId", "github_" + email.split("@")[0]);
        providerInfo.put("avatar_url", "https://avatars.githubusercontent.com/...");
        providerInfo.put("location", "Seoul, South Korea");
        providerInfo.put("public_repos", 42);

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createFacebookUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "FACEBOOK");
        providerInfo.put("providerId", "facebook_" + email.split("@")[0]);
        providerInfo.put("locale", "ko_KR");
        providerInfo.put("timezone", 9);

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createUserWithLocale(String email, String name, String locale) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        providerInfo.put("locale", locale);
        providerInfo.put("email_verified", true);

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createVerifiedUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        providerInfo.put("email_verified", true);  // 인증된 사용자

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createUnverifiedUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        // email_verified 키 없음 (미인증 사용자)

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createVerifiedGoogleUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        providerInfo.put("email_verified", true);  // 인증됨

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }

    private Users createUnverifiedGoogleUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        providerInfo.put("email_verified", false);  // 미인증

        return Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();
    }
}
