# 요구사항: 태그(Tags) 기능

## 1. 데이터 모델 및 Repository
- **`Tag` 엔티티 (`Tag.java`)**
  - `id`: Primary Key (Long)
  - `name`: 태그 이름 (String, Not Null, Unique)
- **`Article`과 `Tag`의 관계 설정**
  - `Article`과 `Tag`는 다대다(Many-to-Many) 관계입니다.
  - `@ManyToMany` 어노테이션과 조인 테이블(`article_tags`)을 사용하여 관계를 설정합니다.
  - `Article` 엔티티에 `tags` 필드 (`Set<Tag>`)를 추가합니다.
- **`TagRepository.java`**
  - `JpaRepository`를 상속받는 인터페이스.
  - 태그 이름으로 `Tag`를 찾는 메소드 필요 (예: `findByName`)
  - 여러 태그 이름을 한 번에 찾는 메소드 필요 (예: `findByNameIn`)

## 2. OpenAPI 명세 (`openapi.yml`) 수정
- **Paths**
  - `GET /api/tags`: 모든 태그 목록 조회 API 경로 (이미 존재하므로 확인).
- **Components (Schemas)**
  - `TagsResponse`: 태그 목록을 담는 응답 객체 정의 (`tags` 필드, `List<String>`).
  - `Article` 모델의 `tagList` 필드(`List<String>`)가 정상적으로 포함되어 있는지 확인.

## 3. API 구현 (Controller, Service)

### 3.1. 태그 목록 조회
- **`TagsController.java` 생성**
  - `TagsApi` 인터페이스를 구현합니다.
  - `GET /api/tags` 요청을 처리하는 `getTags` 메소드를 구현합니다.
- **`TagService.java` 생성**
  - `getTags()`: `TagRepository`에서 모든 태그를 조회하여 `TagsResponse` 형태로 가공하여 반환합니다.

### 3.2. 게시글 생성/수정 시 태그 처리
- **`ArticleService` 수정**
  - `createArticle` 및 `updateArticle` 메소드 수정:
    1. 요청 객체(`NewArticleRequest`, `UpdateArticleRequest`)의 `tagList`를 가져옵니다.
    2. 각 태그 이름에 대해:
       - `TagRepository`에서 해당 이름의 `Tag`가 있는지 조회합니다.
       - 없으면, 새로운 `Tag` 엔티티를 생성하고 저장합니다.
       - 있는 `Tag`와 새로 생성된 `Tag`들을 `Article`의 `tags` Set에 추가/업데이트합니다.
  - `mapToArticleModel` / `mapToArticlesInner` 메소드 수정:
    - `Article` 엔티티의 `tags` Set을 `List<String>` 형태의 `tagList`로 변환하여 응답 모델에 설정합니다.

### 3.3. 태그 필터링 조회
- **`ArticleService`의 `getArticles` 메소드 수정**
  - `tag` 쿼리 파라미터가 존재할 경우:
    - `Specification`을 사용하여, 전달된 `tag` 이름을 가진 `Tag`와 관계를 맺고 있는 `Article`만 조회하도록 검색 조건을 추가합니다.
