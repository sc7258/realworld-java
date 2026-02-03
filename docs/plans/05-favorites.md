# 5. 좋아요(Favorites) API 구현

## 목표
사용자가 게시글에 '좋아요'를 표시하고 취소하는 기능을 구현합니다.

## 세부 작업
- [ ] **Favorite 관계 모델링**
  - `User`와 `Article` 간의 다대다(Many-to-Many) 관계로 '좋아요' 관계 설정
  - `Article` 모델에 `favoritesCount` 필드 추가

- [ ] **API Endpoints 구현**

  - **`POST /api/articles/:slug/favorite` (게시글 '좋아요' 추가)**
    - [ ] JWT 인증 필요
    - [ ] `:slug`으로 게시글 조회
    - [ ] 현재 사용자와 게시글 간의 '좋아요' 관계 생성
    - [ ] `favoritesCount` 업데이트
    - [ ] '좋아요'가 추가된 게시글 정보 반환

  - **`DELETE /api/articles/:slug/favorite` (게시글 '좋아요' 취소)**
    - [ ] JWT 인증 필요
    - [ ] `:slug`으로 게시글 조회
    - [ ] 현재 사용자와 게시글 간의 '좋아요' 관계 제거
    - [ ] `favoritesCount` 업데이트
    - [ ] '좋아요'가 취소된 게시글 정보 반환

## 참고
- **API 명세**: [Realworld API Spec](https://www.realworld.so/docs/specs/backend-specs/articles#favorite-article)
