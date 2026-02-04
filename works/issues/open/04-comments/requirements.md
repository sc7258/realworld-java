# 요구사항: 댓글(Comments) API

## 1. 데이터 모델 및 Repository
- **Comment 엔티티 (`Comment.java`)**
  - `id`: Primary Key (Long)
  - `body`: 댓글 내용 (String, Not Null)
  - `createdAt`: 생성 시각 (Instant, Not Null)
  - `updatedAt`: 수정 시각 (Instant, Not Null)
  - `author`: 작성자 (`User` 엔티티와 Many-to-One 관계)
  - `article`: 댓글이 달린 게시글 (`Article` 엔티티와 Many-to-One 관계)
- **Comment Repository (`CommentRepository.java`)**
  - `JpaRepository`를 상속받는 인터페이스
  - 게시글 ID로 댓글을 찾는 메소드 필요 (예: `findByArticleId`)

## 2. OpenAPI 명세 (`openapi.yml`) 수정
- **Components (Schemas, RequestBodies, Responses)**
  - `Comment`: 댓글 단일 객체 모델 정의
  - `NewCommentRequest`: 댓글 생성 요청 본문 정의 (`comment` 객체 포함, `body` 필드 가짐)
  - `CommentResponse`: 단일 댓글 응답 정의 (`comment` 객체 포함)
  - `MultipleCommentsResponse`: 여러 댓글 목록 응답 정의 (`comments` 배열 포함)
- **Paths**
  - `POST /api/articles/{slug}/comments`: 댓글 생성 API 경로 추가
  - `GET /api/articles/{slug}/comments`: 댓글 목록 조회 API 경로 추가
  - `DELETE /api/articles/{slug}/comments/{id}`: 댓글 삭제 API 경로 추가

## 3. API 구현 (Controller, Service)
- **`POST /api/articles/{slug}/comments` - 댓글 추가**
  - **인증**: JWT 토큰 필수 (인증된 사용자만 댓글 작성 가능)
  - **입력**:
    - `slug`: 댓글을 달 게시글의 slug (경로 변수)
    - `NewCommentRequest`: 댓글 내용 (`body`)
  - **로직**:
    1. `slug`로 `Article` 조회 (없으면 404 Not Found)
    2. 인증된 `User` 정보 조회
    3. `Comment` 엔티티 생성 및 저장
  - **응답**: `CommentResponse` (생성된 댓글 정보)

- **`GET /api/articles/{slug}/comments` - 댓글 목록 조회**
  - **인증**: 선택 사항 (인증/비인증 사용자 모두 조회 가능)
  - **입력**: `slug` (경로 변수)
  - **로직**:
    1. `slug`로 `Article` 조회 (없으면 404 Not Found)
    2. 해당 게시글에 달린 모든 댓글 조회
    3. 각 댓글의 작성자 프로필 정보 포함
  - **응답**: `MultipleCommentsResponse` (댓글 목록)

- **`DELETE /api/articles/{slug}/comments/{id}` - 댓글 삭제**
  - **인증**: JWT 토큰 필수
  - **입력**:
    - `slug`: 게시글 slug (경로 변수)
    - `id`: 삭제할 댓글의 ID (경로 변수)
  - **로직**:
    1. `id`로 `Comment` 조회 (없으면 404 Not Found)
    2. 인증된 사용자가 댓글의 `author`인지 권한 확인 (아니면 403 Forbidden)
    3. 댓글 삭제
  - **응답**: `200 OK` (성공 시, 본문 없음)
