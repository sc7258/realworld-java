# 요구사항: PostgreSQL 지원 추가

1.  **`docker-compose.yml` 파일 생성**:
    -   프로젝트 루트 디렉토리에 `docker-compose.yml` 파일을 생성한다.
    -   `docs/development/postgresql-docker-setup.md` 문서에 명시된 내용에 따라 PostgreSQL 서비스를 정의한다.
    -   데이터베이스 사용자, 비밀번호, 데이터베이스 이름은 문서의 예시를 따른다.

2.  **Spring Boot 설정 프로필 분리**:
    -   기존의 공통 설정을 `application.properties`에 유지한다.
    -   개발 환경(H2 사용)을 위한 `application-dev.properties` 프로필을 생성한다.
    -   Docker PostgreSQL 환경을 위한 `application-docker.properties` 프로필을 생성한다.
    -   `application-docker.properties`에는 PostgreSQL 접속 정보를 포함한다.

3.  **PostgreSQL 의존성 추가**:
    -   `build.gradle` 파일에 PostgreSQL 드라이버 의존성(`org.postgresql:postgresql`)을 추가한다.

4.  **기본 프로필 설정**:
    -   `application.properties`에 `spring.profiles.active=dev`를 추가하여, 별도 설정이 없을 경우 기본적으로 개발(H2) 환경으로 실행되도록 한다.
