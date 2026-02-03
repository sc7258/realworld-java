# API 테스트 방법 (Postman & Newman)

이 프로젝트는 [Postman](https://www.postman.com/)과 커맨드라인 도구인 [Newman](https://github.com/postmanlabs/newman)을 사용하여 API를 테스트합니다. Realworld 프로젝트에서 제공하는 공식 Postman 컬렉션을 사용하여 우리 백엔드 구현이 명세를 잘 따르는지 검증할 수 있습니다.

## 1. Newman 설치

`newman`은 Node.js 기반의 패키지이므로, `npm`을 통해 설치해야 합니다.

```bash
npm install -g newman
```

## 2. API 테스트 실행

테스트를 실행하기 전에, 먼저 Spring Boot 애플리케이션이 실행 중이어야 합니다 (`./gradlew bootRun` 또는 `.\\gradlew.bat bootRun`).

애플리케이션이 실행되면, 프로젝트 루트 디렉터리에서 운영체제에 맞는 스크립트를 실행하여 API 테스트를 진행할 수 있습니다.

### Linux/macOS

```bash
# 실행 권한 부여 (최초 1회)
chmod +x scripts/run-api-tests.sh

# 테스트 실행
./scripts/run-api-tests.sh
```

### Windows

```bash
# 테스트 실행
scripts\\run-api-tests.bat
```

### 참고
- 스크립트는 로컬에서 실행 중인 `http://localhost:8080/api`를 대상으로 테스트를 수행합니다.
- 다른 URL을 대상으로 테스트하려면 `APIURL` 환경 변수를 설정하여 스크립트를 실행하세요.
- 자세한 내용은 프로젝트 루트의 `README.md` 파일에 있는 "API 테스트" 섹션을 참고하세요.
