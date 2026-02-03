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

## API 테스트

Realworld 프로젝트는 [Postman](https://www.postman.com/) 컬렉션과 [Newman](https://github.com/postmanlabs/newman)을 사용하여 API 테스트를 제공합니다. 자세한 내용은 [API 테스트 가이드](./docs/api-testing.md)를 참고하세요.

## 프로젝트 구조 및 OpenAPI 연동 전략

이 프로젝트는 **OpenAPI Generator를 활용한 계약 우선(Contract-First) 개발 방식**을 적극적으로 채택하고 있습니다. `openapi.yml` 명세가 모든 API의 단일 진실 공급원(Single Source of Truth) 역할을 합니다.

### 아키텍처 핵심

1.  **JPA 엔티티와 API 모델의 분리**:
    -   **JPA 엔티티**: 데이터베이스 테이블과 매핑되는 클래스입니다. (예: `users/entity/User.java`)
        - API 모델과의 이름 충돌을 피하기 위해 `entity`와 같은 하위 패키지에 명시적으로 분리합니다.
    -   **API 모델 (DTO)**: `openapi.yml`로부터 생성되며, 클라이언트와 데이터를 주고받는 데 사용됩니다. (예: `build/generated/.../model/User.java`)

2.  **생성된 코드의 적극적인 활용**:
    -   `build.gradle`의 `openApiGenerate` 태스크는 API 인터페이스와 모델 클래스를 모두 생성합니다.
    -   **컨트롤러**는 생성된 **API 인터페이스**(`...Api.java`)를 `implements`하여 API 계약을 준수하도록 강제합니다.
    -   **서비스와 컨트롤러**는 모두 생성된 **API 모델**(`...Request.java`, `...Response.java`, `User.java` 등)을 사용하여 타입 안정성을 보장합니다.

### `build.gradle`의 핵심 설정

성공적인 연동을 위해 `openApiGenerate` 태스크에 다음과 같은 핵심 옵션이 설정되어 있습니다.

-   `apiPackage`, `modelPackage`: 생성된 코드의 패키지를 프로젝트 구조에 맞게 지정하여 이름 충돌을 방지합니다.
-   `interfaceOnly: "true"`: 컨트롤러의 실제 구현 로직을 생성하지 않고, API의 '계약'인 인터페이스만 생성하도록 합니다.

### 최종 프로젝트 구조 예시

```
src/main/java/com/sc7258/realworldjava
├── RealworldJavaApplication.java
|
├── api/
│   ├ (UserAndAuthenticationApi.java)  // build/generated에 생성됨
│   └ (ArticlesApi.java)               // build/generated에 생성됨
├── model/
│   ├ (NewUserRequest.java)            // build/generated에 생성됨
│   ├ (UserResponse.java)              // build/generated에 생성됨
│   └ (Article.java)                   // build/generated에 생성됨
|
├── ... (config, exception, security)
|
└── users/
    ├── entity/
    │   └── User.java                  // DB와 매핑되는 JPA 엔티티 (직접 구현)
    |
    ├── UserRepository.java            // 엔티티를 사용하는 리포지토리
    ├── UserService.java               // 비즈니스 로직 (엔티티 <-> 모델 변환)
    └── UsersController.java           // 생성된 '...Api' 인터페이스를 구현
```

### 주요 기술 스택

- **프레임워크**: [Spring Boot](https://spring.io/projects/spring-boot)
- **데이터베이스**: H2(개발용) 및 PostgreSQL(프로덕션용)과 함께 [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- **인증**: [Spring Security](https://spring.io/projects/spring-security) 및 [JWT](https://jwt.io/)
- **유효성 검사**: [Bean Validation](https.://beanvalidation.org/)
- **빌드 도구**: [Gradle](https://gradle.org/)
- **API 문서**: [Springdoc OpenAPI](https://springdoc.org/)
