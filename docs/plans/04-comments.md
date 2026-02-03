# 4. 댓글(Comments) API 구현

## 목표
게시글에 댓글을 추가하고, 조회하고, 삭제하는 기능을 구현합니다.

## 세부 작업
- [ ] **Comment 모델 및 Repository 생성**
  - `Comment` 엔티티 클래스 정의 (body, author, article 등)
  - `CommentRepository` 인터페이스 생성

- [ ] **API Endpoints 구현**

  - **`POST /api/articles/:slug/comments` (댓글 추가)**
    - [ ] JWT 인증 필요
    - [ ] Request Body 유효성 검사 (body)
    - [ ] `:slug`으로 게시글 조회
    - [ ] 댓글 저장 및 반환

  - **`GET /api/articles/:slug/comments` (댓글 목록 조회)**
    - [ ] `:slug`으로 게시글 조회
    - [ ] 해당 게시글의 모든 댓글 조회 및 반환

  - **`DELETE /api/articles/:slug/comments/:id` (댓글 삭제)**
    - [ ] JWT 인증 및 댓글 작성자 권한 확인
    - [ ] 댓글 삭제

## 참고
- **API 명세**: [Realworld API Spec](https://www.realworld.so/docs/specs/backend-specs/comments)
