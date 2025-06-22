#!/bin/bash

# 테스트용 PostgreSQL 시작 스크립트
echo "🚀 테스트용 PostgreSQL 시작..."

# 기존 컨테이너 정리
docker compose -f docker-compose.test.yml down

# 테스트 DB 시작
docker compose -f docker-compose.test.yml up -d

# 헬스체크 대기
echo "⏳ PostgreSQL 헬스체크 대기 중..."
timeout 30 bash -c 'until docker exec social-auth-postgres-test pg_isready -U test_user -d social_auth_test; do sleep 1; done'

if [ $? -eq 0 ]; then
    echo "✅ 테스트 DB 준비 완료!"
    echo "📍 연결 정보:"
    echo "   Host: localhost"
    echo "   Port: 5433"
    echo "   Database: social_auth_test"
    echo "   Username: test_user"
    echo "   Password: test_password"
else
    echo "❌ 테스트 DB 시작 실패"
    exit 1
fi
