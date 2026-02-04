# 테스트 시나리오: PostgreSQL 지원 추가

## 시나리오 1: Docker Compose를 이용한 PostgreSQL 실행

1.  **Given**: `docker-compose.yml` 파일이 프로젝트 루트에 존재한다.
2.  **When**: 터미널에서 `docker-compose up -d` 명령을 실행한다.
3.  **Then**: `realworld-db`라는 이름의 PostgreSQL 컨테이너가 성공적으로 실행된다.
4.  **And**: 데이터베이스 클라이언트 도구를 사용하여 `localhost:5432`에 접속했을 때, `your_user`/`your_password`로 `realworld` 데이터베이스에 접속할 수 있다.

## 시나리오 2: `docker` 프로필을 사용한 애플리케이션 실행

1.  **Given**: PostgreSQL 컨테이너가 실행 중이다.
2.  **When**: `./gradlew bootRun --args='--spring.profiles.active=docker'` 명령으로 애플리케이션을 실행한다.
3.  **Then**: 애플리케이션이 오류 없이 시작되며, 로그에서 PostgreSQL에 연결되었음을 확인할 수 있다.
4.  **And**: API(예: 사용자 등록, 로그인)가 정상적으로 동작하며, 데이터가 PostgreSQL 데이터베이스에 저장된다.

## 시나리오 3: 기본 `dev` 프로필을 사용한 애플리케이션 실행

1.  **Given**: PostgreSQL 컨테이너의 실행 여부와 관계없이
2.  **When**: `./gradlew bootRun` 명령으로 애플리케이션을 실행한다.
3.  **Then**: 애플리케이션이 기존과 같이 H2 인메모리 데이터베이스를 사용하여 오류 없이 시작된다.
