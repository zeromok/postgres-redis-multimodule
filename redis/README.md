# Redis 모듈

## 목표
- Spring Data Redis 기반 세션/토큰/보안 실습
- 5대 데이터 타입, JWT, 블랙리스트, Rate Limit 등 실전 시나리오

## 환경 구성
- Redis 7-alpine (docker-compose)
- Spring Boot 3.x, Java 17
- JSON 직렬화, 커스텀 RedisTemplate

## 실습/구현 목록

### 1. 기본 연결/설정
- [x] docker-compose로 Redis 환경 구성
- [x] application.yml로 연결 정보 관리
- [x] RedisTemplate 커스텀 (String/JSON)

### 2. 데이터 타입 실습
- [x] String: 토큰/세션 ID 저장
- [x] Hash: 사용자 프로필
- [x] List: 알림 큐
- [x] Set: 권한/태그
- [ ] Sorted Set: 랭킹/타임라인 (예정)

### 3. Spring Data Redis 연동
- [x] @EnableRedisHttpSession
- [x] 세션 set/get API
- [x] TTL/만료 정책 실험

### 4. JWT/세션 관리
- [x] JWT 생성/검증/파싱 서비스
- [x] Redis에 JWT 저장/조회/삭제 (TTL)
- [x] 세션 관리와 JWT 캐싱 비교

### 5. 보안 실습
- [x] JWT 블랙리스트 (TTL, 강제 만료)
- [x] IP 기반 화이트/블랙리스트
- [x] 사용 횟수 제한 (Rate Limit)

## 테스트/실행 방법

```bash
# Redis 컨테이너 실행
docker-compose up -d redis

# 모듈 테스트
./gradlew :redis:test
```

## 참고/트러블슈팅
- JSON 직렬화, TTL, Redis CLI 활용법 등

---

> 상세 코드/설정/실습 시나리오는 본 README와 소스 주석 참고