-- 테스트용 PostgreSQL 초기화 스크립트

-- JSON 확장 기능 활성화 (PostgreSQL 15에서는 기본적으로 포함됨)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 테스트용 시퀀스 및 기본 데이터 설정
-- (필요한 경우 여기에 추가)

-- 테스트 DB 권한 설정
GRANT ALL PRIVILEGES ON DATABASE social_auth_test TO test_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO test_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO test_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO test_user;

-- 향후 생성될 테이블에 대한 권한 미리 부여
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO test_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO test_user;
