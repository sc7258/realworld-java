# 1. 사용자 및 인증 API 구현

## 목표
Realworld API 명세에 따라 사용자 등록, 로그인, 현재 사용자 정보 조회 및 업데이트 기능을 구현합니다.

## 세부 작업
- [ ] **User 모델 및 Repository 생성**
  - `User` 엔티티 클래스 정의 (email, username, password, bio, image 등)
  - `UserRepository` 인터페이스 생성 (Spring Data JPA)

- [ ] **API Endpoints 구현**

  - **`POST /api/users` (사용자 등록)**
    - [ ] Request Body 유효성 검사 (username, email, password)
    - [ ] 비밀번호 해싱 (e.g., BCrypt)
    - [ ] 사용자 정보 저장
    - [ ] JWT 생성 및 반환

  - **`POST /api/users/login` (로그인)**
    - [ ] Request Body 유효성 검사 (email, password)
    - [ ] 사용자 조회 및 비밀번호 검증
    - [ ] JWT 생성 및 반환

  - **`GET /api/user` (현재 사용자 정보 조회)**
    - [ ] JWT 인증 필터 구현
    - [ ] 인증된 사용자 정보 조회 및 반환

  - **`PUT /api/user` (사용자 정보 수정)**
    - [ ] JWT 인증 필터 구현
    - [ ] Request Body 유효성 검사
    - [ ] 인증된 사용자 정보 업데이트
    - [ ] 업데이트된 사용자 정보 반환 (새로운 JWT 포함 가능)

## 참고
- **인증**: JWT (JSON Web Token) 사용
- **API 명세**: [Realworld API Spec](https://www.realworld.so/docs/specs/backend-specs/authentication)
