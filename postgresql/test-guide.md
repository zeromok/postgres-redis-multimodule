# PostgreSQL JSON 기능 테스트 가이드

## 개요
Docker Compose를 사용하여 PostgreSQL 테스트 환경을 구성하고 JSON 연산자 기능을 테스트합니다.

## 테스트 환경 구성

### 1. 수동 실행
```bash
# 테스트 DB 시작
./scripts/test-db-start.sh

# 테스트 실행
./gradlew :postgresql:test

# 테스트 DB 중지
./scripts/test-db-stop.sh
```

### 2. Gradle 태스크 사용
```bash
# 테스트 DB 시작
./gradlew :postgresql:startTestDb

# 테스트 실행 (자동으로 DB 시작/중지)
./gradlew :postgresql:test

# 테스트 DB 중지
./gradlew :postgresql:stopTestDb

# 테스트 DB 재시작 (데이터 초기화)
./gradlew :postgresql:resetTestDb
```

## 테스트 데이터베이스 정보
- **Host**: localhost
- **Port**: 5433 (개발 DB와 분리)
- **Database**: social_auth_test
- **Username**: test_user
- **Password**: test_password

## 테스트되는 JSON 연산자들

### 1. JSON 필드 추출 연산자 (->>)
```sql
-- 프로바이더별 사용자 검색
SELECT * FROM users WHERE provider_info ->> 'provider' = 'GOOGLE';
```

### 2. JSON 포함 관계 연산자 (@>)
```sql
-- 특정 속성을 포함하는 사용자 검색
SELECT * FROM users WHERE provider_info @> '{"locale": "ko_KR"}';
```

### 3. JSON 키 존재 여부 연산자 (?)
```sql
-- 특정 키를 가진 사용자 검색
SELECT * FROM users WHERE provider_info ? 'email_verified';
```

### 4. 복합 JSON 쿼리
```sql
-- 프로바이더와 이메일 인증 상태를 동시에 확인
SELECT * FROM users 
WHERE provider_info ->> 'provider' = 'GOOGLE' 
AND provider_info ->> 'email_verified' = 'true';
```

## 소셜 로그인 시나리오별 테스트 케이스

### Google OAuth
- 프로바이더 식별: `provider_info ->> 'provider' = 'GOOGLE'`
- 이메일 인증 확인: `provider_info ? 'email_verified'`
- 로케일 기반 필터링: `provider_info @> '{"locale": "ko_KR"}'`

### GitHub OAuth
- 저장소 개수 필터링: `(provider_info ->> 'public_repos')::int > 10`
- 위치 기반 검색: `provider_info ->> 'location' LIKE '%Seoul%'`

### Facebook OAuth
- 타임존 기반 필터링: `(provider_info ->> 'timezone')::int = 9`

## 디렉토리 구조
```
postgresql/
├── src/test/
│   ├── java/
│   │   └── com/daniel/practice/postgres/repository/
│   │       └── UserRepositoryJsonTest.java
│   └── resources/
│       ├── application-test.yml
│       └── test-data.sql
├── scripts/
│   ├── test-db-start.sh
│   └── test-db-stop.sh
├── sql/
│   └── init-test.sql
├── docker-compose.test.yml
└── README-TEST.md
```

## 트러블슈팅

### 포트 충돌
- 개발용 PostgreSQL이 5432 포트를 사용하는 경우, 테스트는 5433 포트 사용
- 필요시 `docker-compose.test.yml`에서 포트 변경 가능

### 권한 문제
```bash
chmod +x scripts/test-db-start.sh
chmod +x scripts/test-db-stop.sh
```

### 컨테이너 정리
```bash
# 모든 테스트 관련 컨테이너 정리
docker-compose -f docker-compose.test.yml down -v

# 사용하지 않는 볼륨 정리
docker volume prune
```

## 성능 고려사항

### JSON 인덱스 활용
```sql
-- GIN 인덱스로 JSON 쿼리 성능 최적화
CREATE INDEX idx_users_provider_info_gin ON users USING GIN (provider_info);

-- 특정 키에 대한 B-tree 인덱스
CREATE INDEX idx_users_provider ON users ((provider_info ->> 'provider'));
```

### 대용량 데이터 테스트
```java
@Test
@DisplayName("대용량 JSON 데이터 성능 테스트")
void testLargeJsonDataPerformance() {
    // 10,000개 사용자 데이터로 JSON 쿼리 성능 측정
}
```

이 구성을 통해 Testcontainers 없이도 일관되고 재현 가능한 PostgreSQL JSON 기능 테스트 환경을 구축할 수 있습니다.
