#!/bin/bash

# 테스트용 PostgreSQL 중지 스크립트
echo "🛑 테스트용 PostgreSQL 중지..."

docker compose -f docker-compose.test.yml down

echo "✅ 테스트 DB 중지 완료!"
