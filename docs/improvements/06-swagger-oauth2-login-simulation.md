# 06. Swagger UI에서 OAuth2 Password Flow를 이용한 로그인 경험 개선

## 목표

현재 Swagger UI에서 API를 테스트하려면, 다음과 같은 번거로운 2단계 인증 과정을 거쳐야 한다.

1.  `/api/users/login` 엔드포인트를 직접 실행하여 응답 본문에서 JWT 토큰을 복사한다.
2.  'Authorize' 버튼을 누르고, `Bearer <복사한_토큰>` 형식으로 값을 붙여넣는다.

이 과정은 개발자 경험(DX)을 저해하므로, Swagger UI에서 ID/PW만으로 바로 인증을 처리할 수 있도록 로그인 흐름을 개선하고자 한다.

## 문제점

Swagger UI는 OAuth 2.0의 "Password" Grant Type을 위한 편리한 ID/PW 입력창을 제공한다. 하지만 이 기능은 표준 OAuth 2.0 명세를 따르므로, 우리 시스템의 커스텀 로그인 방식과 호환되지 않는다.

-   **Swagger UI (OAuth2 Password Flow) 기대사항**:
    -   `Content-Type`: `application/x-www-form-urlencoded`
    -   Request Body: `username=...&password=...`
    -   Response Body: `{"access_token": "..."}`
-   **현재 우리 API (`/api/users/login`)**:
    -   `Content-Type`: `application/json`
    -   Request Body: `{"user": {"email": "...", "password": "..."}}`
    -   Response Body: `{"user": {"token": "...", ...}}`

## 해결 방안

완전한 OAuth 2.0을 구현하는 대신, **Swagger UI의 Password Flow UI와 호환되도록 기존 로그인 API를 확장**하는 '시뮬레이션' 방식을 채택한다.

1.  **`UsersController`의 `login` 메소드 수정**:
    -   기존의 `application/json` 방식은 그대로 유지한다.
    -   `application/x-www-form-urlencoded` 형식의 요청을 추가로 처리할 수 있도록 확장한다.
    -   Form 데이터(`username`, `password`)를 파라미터로 받는다.

2.  **새로운 `TokenResponse` DTO 생성**:
    -   Swagger UI가 기대하는 `{"access_token": "..."}` 형식의 응답을 위한 새로운 DTO를 만든다.
    -   Form 요청에 대해서는 이 `TokenResponse`를 반환하도록 `login` 메소드를 수정한다.

3.  **`OpenApiConfig.java` 수정**:
    -   기존의 'Bearer' 인증 스킴 설정을 'OAuth2 Password Flow'를 사용하도록 변경한다.
    -   토큰 발급 URL(`tokenUrl`)을 우리 로그인 API 경로인 `/api/users/login`으로 지정한다.

이러한 변경을 통해, 개발자는 Swagger UI의 'Authorize' 버튼을 눌렀을 때 나타나는 팝업창에 이메일과 비밀번호만 입력하면, 내부적으로 토큰 발급 및 등록이 모두 자동으로 처리되는 편리한 개발 경험을 누릴 수 있게 된다.
