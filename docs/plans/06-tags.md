# 6. 태그(Tags) API 구현

## 목표
게시글에 사용된 모든 태그 목록을 조회하는 기능을 구현합니다.

## 세부 작업
- [ ] **Tag 모델 및 Repository 생성**
  - `Tag` 엔티티 클래스 정의 (name)
  - `Article`과 다대다(Many-to-Many) 관계 설정
  - `TagRepository` 인터페이스 생성

- [ ] **API Endpoints 구현**

  - **`GET /api/tags` (모든 태그 목록 조회)**
    - [ ] 데이터베이스에 저장된 모든 태그를 조회
    - [ ] 태그 이름 목록을 문자열 배열로 반환

## 게시글 생성/수정 시 태그 처리
- 게시글을 생성하거나 수정할 때 `tagList` 필드를 받아서 `Tag` 엔티티를 조회하거나 새로 생성하고, `Article`과의 관계를 설정해야 합니다.

## 참고
- **API 명세**: [Realworld API Spec](https://www.realworld.so/docs/specs/backend-specs/tags)
