# 요구사항 및 구현 항목

## 1. OpenAPI 명세 업데이트 (`openapi.yml`)
- `Comment` 모델 스키마 정의
- `NewCommentRequest` 요청 본문 스키마 정의
- `CommentResponse` 응답 스키마 정의
- `MultipleCommentsResponse` 응답 스키마 정의
- Articles API (`/articles/{slug}/comments`)에 다음 엔드포인트 추가:
    - `POST /api/articles/{slug}/comments`
    - `GET /api/articles/{slug}/comments`
    - `DELETE /api/articles/{slug}/comments/{id}`

## 2. 데이터베이스 모델링 (`entity` 패키지)
- `Comment` 엔티티 클래스 생성 (`comments/entity/Comment.java`)
    - `id`: `Long` (Primary Key)
    - `body`: `String`
    - `createdAt`, `updatedAt`: `Instant`
    - `author`: `User` (Many-to-One 관계)
    - `article`: `Article` (Many-to-One 관계)
- `Article` 엔티티에 `comments` 리스트 추가 (One-to-Many 관계)
- `User` 엔티티에 `comments` 리스트 추가 (One-to-Many 관계)
- `CommentRepository` 인터페이스 생성 (`comments/CommentRepository.java`)

## 3. 비즈니스 로직 구현 (`service` 패키지)
- `CommentService` 클래스 생성 (`comments/CommentService.java`)
    - `addComment(slug, newComment, currentUser)`: 댓글 추가 로직
    - `getCommentsBySlug(slug)`: 댓글 목록 조회 로직
    - `deleteComment(slug, id, currentUser)`: 댓글 삭제 로직
- `Comment` 엔티티와 `Comment` API 모델 간의 변환 로직 구현

## 4. API 컨트롤러 구현 (`controller` 패키지)
- `CommentsController` 클래스 생성 (`comments/CommentsController.java`)
    - 생성된 `CommentsApi` 인터페이스를 구현
    - `POST /api/articles/{slug}/comments`: `CommentService.addComment` 호출
    - `GET /api/articles/{slug}/comments`: `CommentService.getCommentsBySlug` 호출
    - `DELETE /api/articles/{slug}/comments/{id}`: `CommentService.deleteComment` 호출
    - `SecurityContextHolder`를 사용하여 인증된 사용자 정보 조회

## 5. 예외 처리
- `ArticleNotFoundException` 처리
- `CommentNotFoundException` 처리
- 댓글 삭제 권한이 없는 경우 `ForbiddenException` 처리
