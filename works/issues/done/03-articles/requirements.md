# 게시글(Articles) API 구현 요구사항

## 1. Article 모델 및 데이터베이스 설정
-   `Article` JPA 엔티티를 생성합니다.
    -   필수 필드: `id`, `slug`, `title`, `description`, `body`, `createdAt`, `updatedAt`
    -   연관 관계: `author` (작성자, `User` 엔티티와 N:1 관계)
-   `ArticleRepository` Spring Data JPA 리포지토리를 생성합니다.

## 2. API 엔드포인트 구현
-   `openapi.yml`에 Article 관련 API(게시글 생성, 목록 조회, 피드, 단일 조회, 수정, 삭제) 명세를 추가하고, 관련 DTO와 API 인터페이스가 생성되도록 합니다.
-   `ArticlesController`를 생성하고, 생성된 `ArticlesApi` 인터페이스를 구현합니다.

### 2.1. 게시글 생성 (Create Article)
-   **`POST /api/articles`**
-   **인증**: JWT 토큰을 통해 인증된 사용자만 게시글을 생성할 수 있습니다.
-   **요청**: `title`, `description`, `body` 필드를 포함하는 요청 객체를 받습니다. (`tagList`는 선택 사항)
-   **처리**:
    -   `title`을 기반으로 고유한 `slug`를 생성합니다. (예: "how-to-train-your-dragon")
    -   인증된 사용자를 게시글의 `author`로 설정합니다.
    -   데이터베이스에 새로운 `Article`을 저장합니다.
-   **응답**: 생성된 게시글 정보를 `ArticleResponse` 형태로 반환합니다.

### 2.2. 게시글 목록 조회 (List Articles)
-   **`GET /api/articles`**
-   **인증**: 인증은 선택 사항입니다.
-   **필터링**:
    -   `tag`: 특정 태그가 포함된 게시글만 필터링합니다.
    -   `author`: 특정 작성자의 게시글만 필터링합니다.
    -   `favorited`: 특정 사용자가 "좋아요"한 게시글만 필터링합니다.
-   **페이지네이션**:
    -   `limit`: 한 페이지에 보여줄 게시글 수를 제한합니다. (기본값: 20)
    -   `offset`: 조회할 게시글의 시작 위치를 지정합니다. (기본값: 0)
-   **정렬**: 최신순으로 정렬합니다.
-   **응답**: 게시글 목록과 총 게시글 수를 반환합니다.

### 2.3. 게시글 피드 (Get Feed)
-   **`GET /api/articles/feed`**
-   **인증**: JWT 토큰을 통해 인증된 사용자만 피드를 조회할 수 있습니다.
-   **처리**: 현재 사용자가 팔로우하는 모든 사용자의 게시글을 조회합니다.
-   **페이지네이션**: `limit`, `offset`을 지원합니다.
-   **정렬**: 최신순으로 정렬합니다.
-   **응답**: 게시글 목록과 총 게시글 수를 반환합니다.

### 2.4. 단일 게시글 조회 (Get Article)
-   **`GET /api/articles/{slug}`**
-   **인증**: 인증은 선택 사항입니다.
-   **처리**: `slug`를 이용해 특정 게시글을 조회합니다.
-   **응답**: 조회된 게시글 정보를 `ArticleResponse` 형태로 반환합니다. 게시글이 없으면 404 에러를 반환합니다.

### 2.5. 게시글 수정 (Update Article)
-   **`PUT /api/articles/{slug}`**
-   **인증**: JWT 토큰을 통해 인증된 사용자만 게시글을 수정할 수 있습니다.
-   **권한**: 게시글의 `author`와 현재 인증된 사용자가 동일해야 합니다.
-   **요청**: `title`, `description`, `body` 중 변경할 필드를 포함하는 요청 객체를 받습니다.
-   **처리**:
    -   `slug`로 게시글을 조회합니다.
    -   요청된 필드로 게시글 정보를 업데이트합니다.
    -   `title`이 변경되면 `slug`도 함께 업데이트합니다.
-   **응답**: 수정된 게시글 정보를 `ArticleResponse` 형태로 반환합니다.

### 2.6. 게시글 삭제 (Delete Article)
-   **`DELETE /api/articles/{slug}`**
-   **인증**: JWT 토큰을 통해 인증된 사용자만 게시글을 삭제할 수 있습니다.
-   **권한**: 게시글의 `author`와 현재 인증된 사용자가 동일해야 합니다.
-   **처리**: `slug`로 게시글을 조회하여 삭제합니다.
-   **응답**: 성공 시 204 No Content를 반환합니다.
