# 🚀 PostgreSQL & Redis Practice Project

> **포트폴리오용 통합 로그인 플랫폼 개발을 위한 PostgreSQL과 Redis 학습 및 실습 프로젝트**  
> 멀티모듈 구조로 각 기술을 단계별로 학습하고, 최종적으로 소셜 로그인 중계 플랫폼에 통합 적용합니다.

## 📋 프로젝트 개요

### 🎯 목적
- **메인 목표**: 개인 포트폴리오용 통합 로그인 플랫폼 개발 준비
- **학습 방식**: PostgreSQL → Redis → 통합 단계별 실습
- **최종 목표**: 다중 OAuth 프로바이더를 지원하는 소셜 로그인 API 서비스

### 📂 프로젝트 구조
```
postgres-redis-practice/
├── postgresql/                    # PostgreSQL + JPA 학습 모듈 ✅ 완료
│   ├── src/main/java/             # Entity, Repository, Controller
│   ├── src/test/java/             # JSON 연산자 테스트 (4개 통과)
│   └── README-TEST.md             # 테스트 가이드
├── redis/                         # Redis 학습 모듈 🚧 진행중
├── integration/                   # 통합 모듈 (예정)
├── scripts/                       # 테스트 환경 스크립트
│   ├── test-db-start.sh          # 테스트 DB 시작
│   └── test-db-stop.sh           # 테스트 DB 중지
├── sql/
│   └── init-test.sql             # 테스트 DB 초기화
├── docker-compose.test.yml       # 테스트 환경 (포트 5433)
└── docker-compose.yml            # 개발 환경 (포트 5432)
```

## 🛠️ 기술 스택

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **Language** | Java | 17 | 메인 개발 언어 |
| **Framework** | Spring Boot | 3.5.3 | 백엔드 프레임워크 |
| **Database** | PostgreSQL | 15-alpine | 메인 데이터베이스 (JSON 지원) |
| **Cache** | Redis | 7-alpine | 세션/토큰 캐싱 |
| **ORM** | Spring Data JPA | 3.5.3 | 데이터 접근 계층 |
| **Build** | Gradle | 8.14.2 | 빌드 도구 (멀티모듈) |
| **Container** | Docker Compose | - | 환경 구성 및 격리 |

## ✅ 현재 진행 상황

### 🟢 **PostgreSQL 모듈 (완료)**
- [x] **환경 구성**: Docker Compose 기반 개발/테스트 환경 분리
- [x] **Entity 설계**: Users 엔티티 + JSONB 필드 (OAuth 프로바이더 정보)
- [x] **Repository 구현**: PostgreSQL JSON 연산자 활용 쿼리 메서드
- [x] **테스트 작성**: 4가지 JSON 연산자 테스트 케이스 모두 통과 ✅
- [x] **트러블슈팅**: JSON 연산자 `?`와 JPA 파라미터 바인딩 충돌 해결

#### **구현된 주요 기능**
```java
// JSON 연산자 활용 Repository 메서드들
List<Users> findByProvider(String provider);                     // ->> 연산자
List<Users> findByProviderInfoContaining(String criteria);       // @> 연산자  
List<Users> findByProviderInfoHasKey(String key);               // jsonb_exists 함수
List<Users> findVerifiedUsersByProvider(String provider);        // 복합 쿼리
List<Object[]> countUsersByProvider();                          // 집계 쿼리
```

#### **테스트 성과**
- ✅ OAuth 프로바이더별 사용자 검색
- ✅ JSON 포함 관계 검색 (`@>` 연산자)
- ✅ JSON 키 존재 여부 검색 (`jsonb_exists`)
- ✅ 복합 JSON 쿼리 (프로바이더 + 이메일 인증)

### 🟡 **Redis 모듈 (Spring Data Redis 연동 완료)**

#### **환경 구성**
- [x] **Redis Docker Container**: Redis 7-alpine 기반 환경 구성
- [x] **Spring Data Redis 설정**: RedisTemplate 커스텀 설정
- [x] **직렬화 전략**: String/JSON 직렬화 구성

#### **구현된 주요 기능**
```java
// Redis 설정 (JSON 직렬화 지원)
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    // StringRedisSerializer + GenericJackson2JsonRedisSerializer 조합
}

// 5가지 Redis 데이터 타입 활용 실습 완료
- String: JWT 토큰, 세션 ID 저장
- Hash: 사용자 프로필 정보 저장
- List: 알림 큐 구현 (LIFO)
- Set: 사용자 권한 관리
- Sorted Set: 예정 (랭킹, 타임라인)
```

#### **테스트 성과**
- ✅ **String 타입**: 토큰/세션 ID 저장 및 조회
- ✅ **Hash 타입**: 사용자 프로필 정보 저장/조회
- ✅ **List 타입**: LIFO 방식 알림 큐 구현
- ✅ **Set 타입**: 중복 없는 권한 관리
- ✅ **JSON 직렬화**: 복합 객체 저장/조회 검증

#### **다음 구현 예정**
- 🚧 **세션 관리**: Spring Session + Redis 연동
- 🚧 **JWT 토큰 캐싱**: 토큰 저장 및 TTL 관리
- 🚧 **자동 만료 처리**: TTL 기반 토큰 만료

### 🟡 **Redis 모듈 (진행중)**
- [x] **Redis 기본 연결 및 설정** ✅ 완료
- [x] **5가지 데이터 타입 실습** (String, Hash, List, Set, Sorted Set) ✅ 완료
- [x] **Spring Data Redis 연동** ✅ 완료
- [ ] **세션 관리 및 JWT 토큰 캐싱** 🚧 진행 예정
- [ ] **TTL 기반 토큰 만료 처리** 🚧 진행 예정

### 🟡 **Integration 모듈 (예정)**
- [ ] OAuth 프로바이더 연동 (Google, GitHub, Facebook)
- [ ] JWT 토큰 발급 및 Redis 캐싱
- [ ] PostgreSQL JSON 필드에 사용자 정보 저장
- [ ] 통합 소셜 로그인 API 엔드포인트

## 🚀 시작하기

### 1. 환경 요구사항
```bash
# 필수 설치 요소
- Java 17
- Docker & Docker Compose
- Git

# 권장 도구
- IntelliJ IDEA
- DBeaver (PostgreSQL GUI)
- Redis CLI
```

### 2. 프로젝트 클론 및 실행
```bash
# 1. 저장소 클론
git clone <repository-url>
cd postgres-redis-practice

# 2. 개발 환경 시작
docker-compose up -d

# 3. PostgreSQL 모듈 실행
./gradlew :postgresql:bootRun

# 4. 테스트 실행
./gradlew :postgresql:test
```

### 3. 테스트 환경 (분리된 환경)
```bash
# 테스트 DB 시작 (포트 5433)
./scripts/test-db-start.sh

# 테스트 실행
./gradlew :postgresql:test

# 테스트 DB 정리
./scripts/test-db-stop.sh
```

## 🔧 환경 구성

### 포트 매핑
| 서비스 | 개발환경 | 테스트환경 | 용도 |
|--------|----------|------------|------|
| **PostgreSQL** | 5432 | 5433 | 메인 데이터베이스 |
| **Redis** | 6379 | - | 캐시 서버 |
| **Spring Boot** | 8080 | - | 애플리케이션 서버 |

### 데이터베이스 접속 정보
```yaml
# 개발환경
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/practice_db
    username: daniel
    password: password123

# 테스트환경  
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/social_auth_test
    username: test_user
    password: test_password
```

## 📊 핵심 성과 및 학습 포인트

### 🎯 **해결한 주요 기술 이슈**
1. **PostgreSQL JSON 연산자와 JPA 충돌 해결**
   - 문제: `?` 연산자와 JPA 파라미터 바인딩 충돌
   - 해결: `jsonb_exists()` 함수 사용으로 우회

2. **Docker 환경 분리 전략**
   - 개발환경(5432)과 테스트환경(5433) 포트 분리
   - 각 환경별 독립적인 데이터 관리

3. **JSON 스키마 설계**
   - OAuth 프로바이더별 유연한 JSON 구조 설계
   - 타입 일관성 유지 (Boolean vs String 이슈 해결)

### 📈 **성능 최적화 경험**
```sql
-- GIN 인덱스를 활용한 JSON 쿼리 최적화
CREATE INDEX idx_users_provider_info_gin ON users USING GIN(provider_info);

-- 특정 키에 대한 B-tree 인덱스
CREATE INDEX idx_users_provider ON users((provider_info ->> 'provider'));
```

## 🧪 테스트 전략

### 테스트 커버리지
- **PostgreSQL JSON 기능**: 4가지 연산자 모두 테스트 완료
- **테스트 격리**: `@Transactional` + `create-drop` 전략
- **실제 DB 사용**: H2가 아닌 실제 PostgreSQL로 검증

### 테스트 실행 방법
```bash
# 전체 테스트
./gradlew test

# PostgreSQL 모듈 테스트만
./gradlew :postgresql:test

# 특정 테스트 클래스
./gradlew :postgresql:test --tests "UserRepositoryJsonTest"
```

## 📚 학습 자료 및 문서

### 프로젝트 문서
- **[PostgreSQL 테스트 가이드](postgresql/README-TEST.md)**: 테스트 환경 구성 및 실행 방법
- **[트러블슈팅 기록](트러블슈팅-기록.md)**: 발생한 이슈와 해결 과정
- **[JSON 연산자 활용법](JSON-연산자-가이드.md)**: PostgreSQL JSON 기능 완전 가이드

### 외부 참고자료
- [PostgreSQL JSON 공식 문서](https://www.postgresql.org/docs/current/datatype-json.html)
- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Docker Compose 공식 문서](https://docs.docker.com/compose/)

## 🎯 다음 단계 계획

### **Phase 2: Redis 세션/토큰 관리 (진행중)**
1. **✅ Redis 기본 연결 및 설정 완료**
2. **✅ 데이터 타입별 활용법 실습 완료** (String, Hash, List, Set)
3. **✅ Spring Data Redis 연동 완료**
4. **🚧 세션 관리 및 JWT 토큰 캐싱 (다음 작업)**
   - Spring Session + Redis 연동
   - JWT 토큰 저장 및 TTL 관리
   - 자동 만료 처리 구현

### **Phase 3: 소셜 로그인 통합 (최종 목표)**
1. **OAuth 2.0 프로바이더 연동**
   - Google, GitHub, Facebook OAuth
2. **JWT 토큰 발급 및 관리**
   - Redis 기반 토큰 캐싱
   - 자동 만료 처리
3. **통합 API 엔드포인트**
   - RESTful API 설계
   - API 문서화 (Swagger)

## 🤝 기여 및 피드백

### 코드 품질 관리
- **코드 스타일**: Google Java Style Guide
- **커밋 메시지**: Conventional Commits
- **브랜치 전략**: Git Flow

### 이슈 및 개선사항
프로젝트 관련 문의나 개선 제안은 Issues를 통해 남겨주세요.

---

## 📝 라이선스

이 프로젝트는 학습 목적으로 작성되었으며, MIT 라이선스를 따릅니다.

---

**마지막 업데이트**: 2025-06-23  
**현재 상태**: PostgreSQL 모듈 완료, Redis 모듈 Spring Data Redis 연동 완료  
**프로젝트 완성도**: 50% (PostgreSQL 완료 + Redis 기본 연동 완료)
