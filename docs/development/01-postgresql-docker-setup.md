# 01. 로컬 개발 환경: PostgreSQL Docker 설정

이 문서는 Docker Compose를 사용하여 로컬 개발 환경에 PostgreSQL 데이터베이스를 설정하는 방법을 안내합니다.

## 1. 전제 조건

-   [Docker Desktop](https://www.docker.com/products/docker-desktop/)이 설치되어 있어야 합니다.

## 2. `docker-compose.yml` 파일 생성

프로젝트 루트 디렉토리에 다음 내용으로 `docker-compose.yml` 파일을 생성합니다.

```yaml
version: "3.8"
services:
  db:
    image: postgres:14
    container_name: realworld-db
    restart: always
    environment:
      POSTGRES_USER: your_user
      POSTGRES_PASSWORD: your_password
      POSTGRES_DB: realworld
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data

volumes:
  db_data:
```

-   `image`: 사용할 PostgreSQL 이미지 버전을 지정합니다.
-   `environment`: 데이터베이스 사용자, 비밀번호, 기본 데이터베이스 이름을 설정합니다.
-   `ports`: 로컬 PC의 5432 포트와 컨테이너의 5432 포트를 연결합니다.
-   `volumes`: 컨테이너가 삭제되어도 데이터가 보존되도록 Docker 볼륨을 사용합니다.

## 3. Spring Boot 프로필 설정

PostgreSQL에 연결하기 위한 별도의 Spring Boot 프로필을 생성합니다.

### `application-docker.properties`

`src/main/resources/` 경로에 다음 내용으로 `application-docker.properties` 파일을 생성합니다.

```properties
# Docker 환경 프로필 (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/realworld
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 4. 실행 방법

1.  **데이터베이스 시작**: 프로젝트 루트에서 다음 명령을 실행하여 PostgreSQL 컨테이너를 백그라운드로 시작합니다.
    ```bash
    docker-compose up -d
    ```

2.  **애플리케이션 실행**: `docker` 프로필을 활성화하여 애플리케이션을 실행합니다.
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=docker'
    ```

이제 애플리케이션은 Docker 컨테이너에서 실행 중인 PostgreSQL 데이터베이스에 연결됩니다.
