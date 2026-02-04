#!/bin/sh

# newman을 사용하여 Realworld API 테스트 컬렉션을 실행합니다.
# APIURL 환경 변수가 설정되지 않은 경우, 기본값으로 http://localhost:8080/api를 사용합니다.
APIURL=${APIURL:-http://localhost:8080/api}

# --- 추가된 부분: 테스트에 사용할 변수 정의 ---
# 매번 고유한 사용자를 생성하기 위해 현재 시간을 사용합니다.
# 이렇게 하면 반복 테스트 시 "username already taken" 오류를 방지할 수 있습니다.
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
# Postman 컬렉션 내의 {{USERNAME}}, {{EMAIL}}, {{PASSWORD}}가 이 값들로 대체됩니다.
newman run "https://raw.githubusercontent.com/realworld-apps/realworld/main/api/Conduit.postman_collection.json" \
  --global-var "APIURL=$APIURL" \
  --global-var "USERNAME=$USERNAME" \
  --global-var "EMAIL=$EMAIL" \
  --global-var "PASSWORD=$PASSWORD" \
  --reporters cli,junit \
  --reporter-junit-export "newman-report.xml"
