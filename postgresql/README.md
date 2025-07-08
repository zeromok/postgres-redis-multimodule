# PostgreSQL 모듈

## 1. 환경 구성 및 기본 연결

### 체크리스트
- [x] Docker Compose로 PostgreSQL 테스트 환경 분리(5433 포트) ✅
- [x] 테스트용 DB/계정/초기 데이터 스크립트 작성 ✅
- [x] Spring Boot + JPA 연동 및 테스트 설정 ✅

### 핵심 개념
- Docker 기반 DB 격리, 테스트 환경 자동화
- 실제 PostgreSQL 사용(H2 대체), JSONB 지원

---

## 2. JSON 연산자 실습

### 체크리스트
- [x] JSON 필드 추출(->>) 쿼리 테스트 ✅
- [x] JSON 포함(@>) 연산자 테스트 ✅
- [x] JSON 키 존재(?) 연산자 테스트 ✅
- [x] 복합 JSON 조건 쿼리 테스트 ✅

### 핵심 개념
- PostgreSQL의 JSONB 타입과 주요 연산자
- 다양한 소셜 로그인 데이터 구조 대응

---

## 3. Repository/쿼리 메서드 구현

### 체크리스트
- [x] Users 엔티티 + JSONB 필드 설계 ✅
- [x] Spring Data JPA 쿼리 메서드로 JSON 연산자 활용 ✅
- [x] 복합 조건/집계 쿼리 구현 ✅

### 핵심 개념
- JPA에서 PostgreSQL JSON 연산자 활용법
- 동적 쿼리, 집계, 복합 조건 처리

---

## 4. 테스트 및 트러블슈팅

### 체크리스트
- [x] 4가지 JSON 연산자별 테스트 케이스 작성/통과 ✅
- [x] 포트 충돌, 권한 문제 등 트러블슈팅 경험 ✅
- [x] 대용량 데이터/성능 테스트(인덱스 실험) ✅

### 핵심 개념
- 테스트 환경 자동화, 컨테이너 관리
- GIN/B-tree 인덱스, 성능 최적화

---

## 5. 실행/테스트 방법

```bash
# 테스트 DB 컨테이너 시작
./scripts/test-db-start.sh

# 테스트 실행
./gradlew :postgresql:test

# 테스트 DB 중지
./scripts/test-db-stop.sh
```
- 또는 Gradle 태스크로 자동화 가능 (`startTestDb`, `stopTestDb`, `resetTestDb`)

---

## 참고/트러블슈팅

- 포트 충돌: 개발(5432) vs 테스트(5433) 분리
- 권한 문제: `chmod +x scripts/*.sh`
- 컨테이너/볼륨 정리: `docker-compose -f docker-compose.test.yml down -v`, `docker volume prune`
- 성능: GIN 인덱스, B-tree 인덱스 실험

---

## 상세 쿼리/테스트/트러블슈팅

- [test-guide.md](test-guide.md)에서 모든 쿼리 예시, 테스트 시나리오, 트러블슈팅 상세 확인

---

> 실습별 코드/설정/트러블슈팅은 test-guide.md와 소스 주석 참고