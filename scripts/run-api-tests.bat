@echo off
setlocal

rem APIURL 환경 변수가 설정되지 않은 경우 기본값을 사용합니다.
if not defined APIURL (
    set "APIURL=http://localhost:8080/api"
)

echo Running API tests against: %APIURL%

rem newman을 사용하여 Realworld API 테스트 컬렉션을 실행합니다.
rem 결과는 cli와 junit 리포터로 출력되며, newman-report.xml 파일로 저장됩니다.
newman run "https://raw.githubusercontent.com/realworld-apps/realworld/main/api/Conduit.postman_collection.json" ^
  --global-var "APIURL=%APIURL%" ^
  --reporters cli,junit ^
  --reporter-junit-export "newman-report.xml"

endlocal
