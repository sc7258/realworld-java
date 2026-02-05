#!/bin/sh

# ========================================================================
# 1. 데이터베이스 초기화
# ========================================================================
echo "Initializing database..."
echo "Running TRUNCATE on 'users' table..."

# Docker 컨테이너(realworld-db) 안에서 psql 명령을 실행하여 테이블을 초기화합니다.
# POSTGRES_PASSWORD 환경 변수를 사용하여 psql에 비밀번호를 전달합니다.
docker exec -i \
  -e PGPASSWORD=devpass \
  realworld-db \
  psql -U devuser -d realworld -c "TRUNCATE TABLE users RESTART IDENTITY CASCADE;"

# 초기화 성공 또는 실패 메시지 출력
if [ $? -eq 0 ]; then
  echo "Database initialized successfully."
else
  echo "Failed to initialize database. Please check if the Docker container 'realworld-db' is running."
  exit 1
fi

# ========================================================================
# 2. API 테스트 실행
# ========================================================================
# newman을 사용하여 Realworld API 테스트 컬렉션을 실행합니다.
# APIURL 환경 변수가 설정되지 않은 경우, 기본값으로 http://localhost:8080/api를 사용합니다.
APIURL=${APIURL:-http://localhost:8080/api}

# 매번 고유한 사용자를 생성하기 위해 현재 시간을 사용합니다.
TIMESTAMP=$(date +%s)
USERNAME="user${TIMESTAMP}"
EMAIL="user${TIMESTAMP}@example.com"
PASSWORD="password"

echo "========================================================================"
echo " API Test Run"
echo "========================================================================"
echo "API URL : $APIURL"
echo "Username: $USERNAME"
echo "Email   : $EMAIL"
echo "========================================================================"

# newman 실행 시 --global-var 옵션으로 위에서 정의한 변수들을 전달합니다.
newman run "https://raw.githubusercontent.com/realworld-apps/realworld/main/api/Conduit.postman_collection.json" \
  --global-var "APIURL=$APIURL" \
  --global-var "USERNAME=$USERNAME" \
  --global-var "EMAIL=$EMAIL" \
  --global-var "PASSWORD=$PASSWORD" \
  --reporters cli,junit \
  --reporter-junit-export "newman-report.xml"
