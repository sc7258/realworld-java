# 3. 게시글(Articles) API 구현

## 목표
게시글 생성, 조회, 수정, 삭제 및 피드 기능을 구현합니다.

## 세부 작업
- [ ] **Article 모델 및 Repository 생성**
  - `Article` 엔티티 클래스 정의 (slug, title, description, body, author, tags 등)
  - `ArticleRepository` 인터페이스 생성

- [ ] **API Endpoints 구현**

  - **`POST /api/articles` (게시글 생성)**
    - [ ] JWT 인증 필요
    - [ ] Request Body 유효성 검사
    - [ ] `slug` 생성 로직 구현 (e.g., title을 기반으로)
    - [ ] 게시글 저장 및 반환

  - **`GET /api/articles` (게시글 목록 조회)**
    - [ ] 필터링 기능 구현 (tag, author, favorited by)
    - [ ] 페이지네이션 구현 (limit, offset)
    - [ ] 결과 정렬 (최신순)

  - **`GET /api/articles/feed` (팔로우하는 사용자들의 게시글 피드)**
    - [ ] JWT 인증 필요
    - [ ] 현재 사용자가 팔로우하는 사용자들의 게시글 목록 조회
    - [ ] 페이지네이션 및 정렬 구현

  - **`GET /api/articles/:slug` (단일 게시글 조회)**
    - [ ] `:slug`으로 게시글 조회
    - [ ] 게시글 정보 반환

  - **`PUT /api/articles/:slug` (게시글 수정)**
    - [ ] JWT 인증 및 작성자 권한 확인
    - [ ] Request Body 유효성 검사
    - [ ] 게시글 정보 업데이트 (slug도 업데이트될 수 있음)
    - [ ] 수정된 게시글 반환

  - **`DELETE /api/articles/:slug` (게시글 삭제)**
    - [ ] JWT 인증 및 작성자 권한 확인
    - [ ] 게시글 삭제

## 참고
- **API 명세**: [Realworld API Spec](https://www.realworld.so/docs/specs/backend-specs/articles)
