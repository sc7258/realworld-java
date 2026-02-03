# 요구사항

- [x] **User 모델 및 Repository 생성**
  - [x] `User` 엔티티 클래스 정의 (`id`, `email`, `username`, `password`, `bio`, `image`)
  - [x] `UserRepository` 인터페이스 생성 (Spring Data JPA)

- [x] **JWT(JSON Web Token) 관련 클래스 생성**
  - [x] JWT 생성, 파싱, 유효성 검증을 담당하는 `JwtUtils` 클래스 구현
  - [x] Spring Security의 `UserDetailsService` 를 구현하여 사용자 정보를 로드하는 서비스 생성
  - [x] JWT 인증을 처리하는 `JwtAuthenticationFilter` 필터 구현

- [x] **API Endpoints 구현**
  - [x] **`POST /api/users` (사용자 등록)**
    - [x] Request DTO (`RegisterUserRequest`) 생성 및 유효성 검사
    - [x] 비밀번호는 `BCryptPasswordEncoder`를 사용하여 암호화
    - [x] 이메일과 사용자 이름은 중복될 수 없음
    - [x] 사용자 정보 저장 후, 생성된 사용자와 JWT를 포함한 Response DTO (`UserResponse`) 반환
  - [x] **`POST /api/users/login` (로그인)**
    - [x] Request DTO (`LoginUserRequest`) 생성 및 유효성 검사
    - [x] 이메일로 사용자를 찾고 비밀번호 일치 여부 확인
    - [x] 로그인 성공 시, 사용자 정보와 JWT를 포함한 `UserResponse` 반환
  - [x] **`GET /api/user` (현재 사용자 정보 조회)**
    - [x] `Authorization` 헤더의 JWT를 통해 인증된 사용자 정보 조회
    - [x] 조회된 사용자 정보와 JWT를 포함한 `UserResponse` 반환
  - [x] **`PUT /api/user` (사용자 정보 수정)**
    - [x] `Authorization` 헤더의 JWT를 통해 인증된 사용자 정보 조회
    - [x] Request DTO (`UpdateUserRequest`) 생성 및 유효성 검사
    - [x] 요청된 정보로 사용자 정보 업데이트 (null이 아닌 필드만)
    - [x] 업데이트된 사용자 정보와 JWT를 포함한 `UserResponse` 반환

- [x] **Spring Security 설정**
  - [x] `SecurityFilterChain`을 설정하여 API 엔드포인트별 접근 권한 설정
    - `POST /api/users`, `POST /api/users/login` : 모두 허용
    - `GET /api/user`, `PUT /api/user` : 인증된 사용자만 허용
  - [x] `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 추가
