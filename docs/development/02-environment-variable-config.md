# 02. 설정 외부화: 환경 변수 사용

이 문서는 환경에 따라 달라지는 설정(특히 민감 정보)을 코드와 분리하기 위해 환경 변수를 사용하는 방법을 안내합니다. 이는 "12-Factor App" 원칙을 따르는 모범 사례입니다.

## 1. 기본 원칙

-   **소스 코드에는 절대 민감 정보를 하드코딩하지 않습니다.** (예: DB 비밀번호, API 키)
-   환경별 설정은 환경 변수를 통해 외부에서 주입합니다.
-   로컬 개발의 편의성을 위해 `.env` 파일을 사용하며, 이 파일은 Git에 커밋하지 않습니다.

## 2. 적용 방법

### 2.1. `docker-compose.yml` 수정

하드코딩된 값을 환경 변수 참조(`$`)로 변경합니다. Docker Compose는 실행 시 자동으로 `.env` 파일이나 호스트 머신의 환경 변수를 읽어 이 값들을 채웁니다.

```yaml
# Before
# environment:
#   POSTGRES_USER: your_user
#   POSTGRES_PASSWORD: your_password

# After
environment:
  POSTGRES_USER: ${POSTGRES_USER}
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  POSTGRES_DB: ${POSTGRES_DB:-realworld} # 변수가 없을 경우 기본값으로 'realworld' 사용
```

### 2.2. Spring Boot 프로필 수정 (`application-docker.properties`)

Spring Boot 역시 `${...}` 구문을 통해 환경 변수를 직접 읽을 수 있습니다.

```properties
# Before
# spring.datasource.username=your_user
# spring.datasource.password=your_password

# After
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.datasource.url=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
```

### 2.3. `.env` 파일 생성 (로컬 개발용)

프로젝트 루트에 로컬 개발에 사용할 환경 변수 값을 정의하는 `.env` 파일을 생성합니다.

```
# For Local Development with Docker Compose
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
POSTGRES_DB=realworld
```

### 2.4. `.gitignore` 파일에 `.env` 추가

`.env` 파일은 민감 정보를 포함할 수 있으므로, Git 저장소에 포함되지 않도록 `.gitignore` 파일에 다음 한 줄을 추가합니다.

```
.env
```

이러한 구성을 통해, 소스 코드를 전혀 변경하지 않고도 각기 다른 환경(local, QA, prod)에서 다른 설정값으로 애플리케이션을 실행할 수 있는 유연하고 안전한 구조가 완성됩니다.
