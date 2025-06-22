# PostgreSQL & Redis Practice Project

OPostgreSQL과 Redis 학습 프로젝트입니다.

## 📁 프로젝트 구조

```
postgres-redis-practice/
├── postgres/         # PostgreSQL 학습 모듈 (Port: 8082)
├── redis/           # Redis 학습 모듈 (Port: 8083)
├── integration/     # 통합 학습 모듈 (Port: 8080)
├── docker-compose.yml
└── README.md
```

## 🛠️ 기술 스택

- **Java**: 17
- **Spring Boot**: 3.5.3
- **PostgreSQL**: 15
- **Redis**: 7
- **Docker**: 환경 구성

## 🚀 시작하기

### 1. 데이터베이스 환경 구축
```bash
# Docker로 PostgreSQL과 Redis 실행
docker-compose up -d

# 상태 확인
docker-compose ps
```

### 2. 각 모듈 실행
```bash
# PostgreSQL 모듈 실행
./gradlew :postgres:bootRun

# Redis 모듈 실행  
./gradlew :redis:bootRun

# 통합 모듈 실행
./gradlew :integration:bootRun
```

## 📚 학습 목표

### PostgreSQL 모듈
- [x] 기본 환경 구축
- [ ] Entity 설계 및 JPA 연동
- [ ] 복잡한 쿼리 및 조인 최적화
- [ ] 인덱스 설계 및 성능 측정
- [ ] JSON 타입 활용
- [ ] 트랜잭션 관리

### Redis 모듈
- [x] 기본 환경 구축
- [ ] 5가지 데이터 타입 실습
- [ ] TTL 및 만료 정책
- [ ] Spring Data Redis 연동
- [ ] Pub/Sub 패턴
- [ ] 분산 락 구현

### Integration 모듈
- [x] 기본 환경 구축
- [ ] @Cacheable, @CacheEvict 활용
- [ ] Cache-aside 패턴 구현
- [ ] 캐시 일관성 문제 해결
- [ ] 성능 비교 테스트
- [ ] 캐시 무효화 전략

## 🔧 개발 환경

### 포트 구성
- **PostgreSQL**: 5432
- **Redis**: 6379
- **Redis Commander**: 8081 (웹 UI)
- **postgres-module**: 8082
- **redis-module**: 8083
- **integration-module**: 8080

### 접속 정보
- **PostgreSQL**: `jdbc:postgresql://localhost:5432/practice_db`
- **사용자**: daniel / password123
- **Redis**: localhost:6379

## 📊 모니터링

### Redis Commander
Redis 데이터를 웹 UI로 확인 가능
- URL: http://localhost:8081

### 로그 확인
```bash
# Docker 로그 확인
docker-compose logs postgres
docker-compose logs redis

# 애플리케이션 로그는 각 모듈 실행 시 콘솔에서 확인
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :postgres:test
./gradlew :redis:test
./gradlew :integration:test
```