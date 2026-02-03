# Realworld Backend (Java & Spring Boot)

> ### [Conduit](https://demo.realworld.io/) 코드베이스는 [Realworld API 사양](https://www.realworld.so/)을 준수하는 실제 예제(CRUD, 인증, 고급 패턴 등)를 포함하고 있습니다.

## "Realworld" 프로젝트란?

"Realworld" 프로젝트는 "Hello, world!" 예제를 넘어서, 실제 세계의 복잡성을 다루는 애플리케이션을 어떻게 구축하는지 보여주기 위해 만들어졌습니다. 이 프로젝트의 목표는 블로그 플랫폼인 [Medium.com](https://medium.com/)의 클론을 만드는 것입니다.

주요 특징은 다음과 같습니다:

- **표준화된 명세**: 프론트엔드와 백엔드 API, 데이터 모델 등이 명확하게 정의되어 있습니다.
- **다양한 기술 스택**: 동일한 명세를 기반으로 여러 프론트엔드 및 백엔드 기술 스택의 구현체들이 존재합니다. (예: React, Angular, Node.js, Django 등)
- **학습 도구**: 개발자들은 이 프로젝트를 통해 특정 기술 스택으로 실제 애플리케이션을 어떻게 구성하고 개발하는지 배울 수 있습니다.

이 저장소는 **Java와 Spring Boot**를 사용하여 Realworld 백엔드 명세를 구현한 버전입니다.

---

이 코드베이스는 CRUD 작업, 인증, 라우팅, 페이지네이션 등을 포함하여 **Java 및 Spring Boot**로 구축된 완전한 풀스택 애플리케이션을 시연하기 위해 만들어졌습니다.

저희는 Java 및 Spring Boot 커뮤니티 스타일 가이드와 모범 사례를 준수하기 위해 많은 노력을 기울였습니다.

realworld로 앱을 빌드하는 방법에 대한 자세한 내용은 [Realworld](https://github.com/realworld-apps/realworld) 사양을 확인하십시오.

## 실행 방법

이것은 표준 Spring Boot 애플리케이션입니다. IDE 또는 명령줄에서 실행할 수 있습니다.

### 전제 조건

- Java 17 이상
- Gradle

### 애플리케이션 실행

1.  저장소 복제:
    ```bash
    git clone https://github.com/sc7258/realworld-java.git
    cd realworld-java
    ```

2.  Gradle 래퍼를 사용하여 애플리케이션 실행:
    - **Linux/macOS:**
      ```bash
      ./gradlew bootRun
      ```
    - **Windows:**
      ```bash
      .\\gradlew.bat bootRun
      ```

애플리케이션은 `http://localhost:8080`에서 시작됩니다.

## 평가 방법

이 구현이 Realworld 명세를 얼마나 잘 준수하는지는 다음 두 가지 방법으로 평가할 수 있습니다.

### 1. API 테스트 (Postman/Newman)

가장 중요한 평가 기준은 Realworld에서 제공하는 공식 API 테스트 스위트를 통과하는 것입니다. 이 테스트는 API의 모든 기능이 명세대로 정확히 동작하는지 검증합니다.

프로젝트에 포함된 테스트 스크립트를 실행하여 모든 테스트가 통과하는지 확인해야 합니다.

### 2. 프론트엔드 연동 테스트

개발된 백엔드는 기존의 다양한 Realworld 프론트엔드 애플리케이션과 호환되어야 합니다.

- **[Realworld 프론트엔드 목록](https://codebase.show/projects/realworld?category=frontend)**

위 목록에서 원하는 프론트엔드 프로젝트를 선택하여 로컬에서 실행한 후, API 서버 주소를 로컬 백엔드(`http://localhost:8080/api`)로 지정합니다. 회원가입, 글 작성, 댓글, 팔로우 등 모든 기능이 정상적으로 동작하는지 확인해야 합니다.

## API 테스트

Realworld 프로젝트는 [Postman](https://www.postman.com/) 컬렉션과 [Newman](https://github.com/postmanlabs/newman)을 사용하여 API 테스트를 제공합니다.

### 전제 조건

- [Node.js 및 npm](https://nodejs.org/ko/download/) 설치
- Newman 설치:
  ```bash
  npm install -g newman
  ```

### 테스트 실행

1.  Spring Boot 애플리케이션이 `http://localhost:8080`에서 실행 중인지 확인합니다.
2.  프로젝트 루트 디렉터리에서 운영체제에 맞는 스크립트를 실행합니다:

    - **Linux/macOS:**
      ```bash
      # 실행 권한 부여 (최초 1회)
      chmod +x scripts/run-api-tests.sh
      # 테스트 실행
      ./scripts/run-api-tests.sh
      ```
      다른 URL로 테스트하려면 `APIURL` 환경 변수를 설정합니다:
      ```bash
      APIURL=http://localhost:3000/api ./scripts/run-api-tests.sh
      ```

    - **Windows:**
      ```bash
      # 테스트 실행
      scripts\\run-api-tests.bat
      ```
      다른 URL로 테스트하려면 `APIURL` 환경 변수를 설정합니다:
      ```bash
      set APIURL=http://localhost:3000/api
      scripts\\run-api-tests.bat
      ```

테스트 결과는 터미널에 표시되며, `newman-report.xml` 파일로도 생성됩니다.

## 프로젝트 구조

- **프레임워크**: [Spring Boot](https://spring.io/projects/spring-boot)
- **데이터베이스**: H2(개발용) 및 PostgreSQL(프로덕션용)과 함께 [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- **유효성 검사**: [Bean Validation](https://beanvalidation.org/)
- **빌드 도구**: [Gradle](https://gradle.org/)

이 프로젝트는 Spring Boot 애플리케이션의 표준 구조를 따릅니다.
