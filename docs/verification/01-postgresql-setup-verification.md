# 검증 가이드: PostgreSQL 연동 설정

이 문서는 `07-add-postgresql-support` 이슈를 통해 구현된 PostgreSQL 연동 기능이 올바르게 설정되고 동작하는지 검증하는 절차를 안내합니다.

## 검증 목표

- Docker Compose를 통해 PostgreSQL 데이터베이스가 정상적으로 실행되는지 확인한다.
- Spring Boot 애플리케이션이 `docker` 프로필을 사용하여 실행되고, H2가 아닌 PostgreSQL에 연결되는지 확인한다.
- API를 통해 생성된 데이터가 PostgreSQL 데이터베이스에 실제로 저장(영속화)되는지 확인한다.

## 전제 조건

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)이 설치 및 **실행 중**이어야 합니다.
- 프로젝트 코드가 로컬에 복제되어 있어야 합니다.

## 검증 절차

### 1단계: 데이터베이스 컨테이너 실행 및 확인

1.  **데이터베이스 시작**:
    프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 PostgreSQL 컨테이너를 시작합니다.
    ```bash
    docker-compose up -d
    ```

2.  **컨테이너 상태 확인**:
    다음 명령어를 실행하여 `realworld-db` 컨테이너가 정상적으로 실행 중인지 확인합니다.
    ```bash
    docker ps
    ```
    **[예상 결과]**
    `realworld-db`라는 이름(NAMES)의 컨테이너가 `Up` 상태(STATUS)로 목록에 표시되어야 합니다.

### 2단계: `docker` 프로필로 애플리케이션 실행 및 확인

1.  **애플리케이션 실행**:
    다음 중 편한 방법으로 `docker` 프로필을 활성화하여 애플리케이션을 실행합니다.
    - **터미널**: `.\\gradlew.bat bootRun --args='--spring.profiles.active=docker'`
    - **IntelliJ**: `Run/Debug Configurations`에서 `Active profiles`에 `docker`를 추가하고 실행.

2.  **로그 확인**:
    애플리케이션 실행 로그에서 데이터베이스 연결 관련 부분을 확인합니다.

    **[예상 결과]**
    - 오류 없이 애플리케이션이 시작되어야 합니다.
    - 로그에서 `HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect` 와 같이 PostgreSQL Dialect를 사용한다는 메시지가 보여야 합니다. (H2Dialect가 아니어야 함)

### 3단계: API를 통한 데이터 생성

1.  **회원가입 API 호출**:
    애플리케이션이 실행 중인 상태에서, 새 터미널을 열어 다음 `curl` 명령어를 실행하여 새로운 사용자를 등록합니다.
    ```bash
    curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"user":{"email":"verify@test.com", "password":"password123", "username":"verifier"}}'
    ```

2.  **응답 확인**:

    **[예상 결과]**
    - HTTP 상태 코드 `201 Created`와 함께 다음과 같은 JSON 응답이 반환되어야 합니다.
    ```json
    {
      "user": {
        "email": "verify@test.com",
        "username": "verifier",
        "bio": null,
        "image": null,
        "token": "..."
      }
    }
    ```

### 4단계: 데이터베이스에서 실제 데이터 확인

1.  **DB 클라이언트 연결**:
    DBeaver, DataGrip, pgAdmin 등 선호하는 데이터베이스 클라이언트 도구를 사용하여 아래 정보로 PostgreSQL에 접속합니다.
    - **Host**: `localhost`
    - **Port**: `5432`
    - **Database**: `realworld`
    - **Username**: `devuser`
    - **Password**: `devpass`

2.  **데이터 조회**:
    연결에 성공하면, 다음 SQL 쿼리를 실행합니다.
    ```sql
    SELECT * FROM users WHERE email = 'verify@test.com';
    ```

3.  **결과 확인**:

    **[예상 결과]**
    - 방금 API를 통해 가입한 `verifier` 사용자의 정보가 테이블에 저장되어 있는 것을 눈으로 직접 확인할 수 있어야 합니다.

---

위 4단계를 모두 성공적으로 통과했다면, PostgreSQL 연동 기능이 올바르게 구현되고 동작하는 것이 검증된 것입니다.
