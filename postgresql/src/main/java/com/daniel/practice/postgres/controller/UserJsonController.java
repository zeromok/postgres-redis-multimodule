package com.daniel.practice.postgres.controller;

import com.daniel.practice.postgres.entity.Users;
import com.daniel.practice.postgres.enums.UserStatus;
import com.daniel.practice.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PostgreSQL JSON 기능 실습용 Controller
 * 
 * 실제 OAuth 시나리오를 기반으로 JSON 연산자들을 테스트할 수 있는 API 제공
 */
@RestController
@RequestMapping("/api/users/json")
@RequiredArgsConstructor
@Slf4j
public class UserJsonController {

    private final UserRepository userRepository;

    /**
     * 테스트 데이터 생성 API
     * 다양한 OAuth 프로바이더 사용자들을 생성
     */
    @PostMapping("/test-data")
    public ResponseEntity<String> createTestData() {
        // Google 사용자들 생성
        createGoogleUser("google1@example.com", "Google User 1", true);
        createGoogleUser("google2@example.com", "Google User 2", false);
        createGoogleUser("google3@example.com", "Google User 3", true);

        // GitHub 사용자들 생성
        createGitHubUser("github1@example.com", "GitHub User 1");
        createGitHubUser("github2@example.com", "GitHub User 2");

        // Facebook 사용자 생성
        createFacebookUser("facebook1@example.com", "Facebook User 1");

        log.info("테스트 데이터 생성 완료: Google(3), GitHub(2), Facebook(1)");
        return ResponseEntity.ok("테스트 데이터 생성 완료: 총 6명의 사용자");
    }

    /**
     * JSON 연산자 (->>): 프로바이더별 사용자 검색
     * 
     * 예시: GET /api/users/json/provider/GOOGLE
     */
    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<Users>> findByProvider(@PathVariable String provider) {
        log.info("프로바이더별 사용자 검색: {}", provider);
        
        List<Users> users = userRepository.findByProvider(provider);
        
        log.info("검색 결과: {}명의 {} 사용자", users.size(), provider);
        return ResponseEntity.ok(users);
    }

    /**
     * JSON 포함 관계 검색 (@>): 특정 속성을 가진 사용자 검색
     * 
     * 예시: POST /api/users/json/containing
     * Body: {"locale": "ko_KR"}
     */
    @PostMapping("/containing")
    public ResponseEntity<List<Users>> findByProviderInfoContaining(@RequestBody String criteria) {
        log.info("JSON 포함 관계 검색: {}", criteria);
        
        List<Users> users = userRepository.findByProviderInfoContaining(criteria);
        
        log.info("검색 결과: {}명의 사용자", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * JSON 키 존재 여부 검색 (?): 특정 키를 가진 사용자 검색
     * 
     * 예시: GET /api/users/json/has-key/email_verified
     */
    @GetMapping("/has-key/{key}")
    public ResponseEntity<List<Users>> findByProviderInfoHasKey(@PathVariable String key) {
        log.info("JSON 키 존재 여부 검색: {}", key);
        
        List<Users> users = userRepository.findByProviderInfoHasKey(key);
        
        log.info("검색 결과: {}명의 사용자가 '{}' 키를 보유", users.size(), key);
        return ResponseEntity.ok(users);
    }

    /**
     * 복합 JSON 쿼리: 프로바이더와 이메일 인증 상태 동시 확인
     * 
     * 예시: GET /api/users/json/verified/GOOGLE
     */
    @GetMapping("/verified/{provider}")
    public ResponseEntity<List<Users>> findVerifiedUsersByProvider(@PathVariable String provider) {
        log.info("인증된 사용자 검색: {} 프로바이더", provider);
        
        List<Users> users = userRepository.findVerifiedUsersByProvider(provider);
        
        log.info("검색 결과: {}명의 인증된 {} 사용자", users.size(), provider);
        return ResponseEntity.ok(users);
    }

    /**
     * JSON 집계: 프로바이더별 사용자 수 통계
     * 
     * 예시: GET /api/users/json/stats/providers
     */
    @GetMapping("/stats/providers")
    public ResponseEntity<Map<String, Object>> getProviderStats() {
        log.info("프로바이더별 사용자 통계 조회");
        
        List<Object[]> rawResults = userRepository.countUsersByProvider();
        
        Map<String, Object> stats = new HashMap<>();
        for (Object[] result : rawResults) {
            String provider = (String) result[0];
            Long count = (Long) result[1];
            stats.put(provider, count);
        }
        
        log.info("통계 결과: {}", stats);
        return ResponseEntity.ok(stats);
    }

    /**
     * 모든 사용자 조회 (디버깅용)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Users>> findAllUsers() {
        List<Users> users = userRepository.findAll();
        log.info("전체 사용자 수: {}", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * 모든 데이터 삭제 (테스트 초기화용)
     */
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllUsers() {
        long count = userRepository.count();
        userRepository.deleteAll();
        log.info("모든 사용자 데이터 삭제 완료: {}명", count);
        return ResponseEntity.ok("모든 데이터 삭제 완료: " + count + "명");
    }

    // ================================
    // 테스트 데이터 생성 헬퍼 메서드들
    // ================================

    private void createGoogleUser(String email, String name, boolean emailVerified) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GOOGLE");
        providerInfo.put("providerId", "google_" + email.split("@")[0]);
        providerInfo.put("locale", "ko_KR");
        providerInfo.put("picture", "https://lh3.googleusercontent.com/...");
        providerInfo.put("email_verified", emailVerified);
        providerInfo.put("family_name", "Kim");
        providerInfo.put("given_name", name.split(" ")[1]);

        Users user = Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();

        userRepository.save(user);
    }

    private void createGitHubUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "GITHUB");
        providerInfo.put("providerId", "github_" + email.split("@")[0]);
        providerInfo.put("avatar_url", "https://avatars.githubusercontent.com/...");
        providerInfo.put("location", "Seoul, South Korea");
        providerInfo.put("public_repos", 42);
        providerInfo.put("followers", 123);
        providerInfo.put("following", 89);

        Users user = Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();

        userRepository.save(user);
    }

    private void createFacebookUser(String email, String name) {
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("provider", "FACEBOOK");
        providerInfo.put("providerId", "facebook_" + email.split("@")[0]);
        providerInfo.put("locale", "ko_KR");
        providerInfo.put("timezone", 9);
        providerInfo.put("age_range", Map.of("min", 21));

        Users user = Users.builder()
                .email(email)
                .name(name)
                .status(UserStatus.ACTIVE)
                .providerInfo(providerInfo)
                .build();

        userRepository.save(user);
    }
}
