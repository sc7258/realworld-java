# 2. 프로필 및 팔로우 API 구현

## 목표
다른 사용자의 프로필을 조회하고, 팔로우/언팔로우하는 기능을 구현합니다.

## 세부 작업
- [ ] **Profile 모델 확장**
  - `User` 모델에 팔로우 관계(following) 필드 추가 또는 별도의 `Follow` 엔티티 생성
  - 프로필 조회 시 팔로우 여부를 나타내는 `following` boolean 필드 동적 계산

- [ ] **API Endpoints 구현**

  - **`GET /api/profiles/:username` (프로필 조회)**
    - [ ] `:username`으로 사용자 조회
    - [ ] (인증된 경우) 현재 사용자가 해당 프로필을 팔로우하는지 여부 확인
    - [ ] 프로필 정보(username, bio, image, following) 반환

  - **`POST /api/profiles/:username/follow` (프로필 팔로우)**
    - [ ] JWT 인증 필요
    - [ ] `:username`으로 팔로우할 사용자 조회
    - [ ] 현재 사용자와의 팔로우 관계 생성
    - [ ] 팔로우된 사용자의 프로필 정보 반환

  - **`DELETE /api/profiles/:username/follow` (프로필 언팔로우)**
    - [ ] JWT 인증 필요
    - [ ] `:username`으로 언팔로우할 사용자 조회
    - [ ] 현재 사용자와의 팔로우 관계 제거
    - [ ] 언팔로우된 사용자의 프로필 정보 반환

## 참고
- **API 명세**: [Realworld API Spec](https://www.realworld.so/docs/specs/backend-specs/profiles)
