#!/bin/sh

# newman을 사용하여 Realworld API 테스트 컬렉션을 실행합니다.
# APIURL 환경 변수가 설정되지 않은 경우, 기본값으로 http://localhost:8080/api를 사용합니다.
APIURL=${APIURL:-http://localhost:8080/api}

echo "Running API tests against: $APIURL"

# newman 실행. cli와 junit 리포터를 사용하고, 결과를 newman-report.xml 파일로 저장합니다.
newman run "https://raw.githubusercontent.com/realworld-apps/realworld/main/api/Conduit.postman_collection.json" \
  --global-var "APIURL=$APIURL" \
  --reporters cli,junit \
  --reporter-junit-export "newman-report.xml"
