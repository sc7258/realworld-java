# 5. 게시글 수정 시 태그(Tag) 업데이트 기능 추가

## 현상 및 문제점

현재 프로젝트가 따르고 있는 Realworld API 명세는 게시글 수정(`PUT /api/articles/{slug}`) 시 `title`, `description`, `body` 필드만 변경할 수 있도록 정의되어 있습니다. `tagList` 필드는 요청 본문에 포함되지 않아, 사용자가 게시글을 발행한 후에는 태그를 수정하거나 추가/삭제할 수 없습니다.

이는 다음과 같은 불편함을 야기합니다.
-   태그에 오타가 있거나 부적절한 태그를 사용했을 경우 수정이 불가능합니다.
-   시간이 지나 글의 내용이 보강되면서 더 적합한 태그를 추가하고 싶어도 할 수 없습니다.
-   결과적으로 콘텐츠의 검색 가능성과 관리 유연성이 떨어집니다.

## 개선 목표

사용자가 게시글 수정 시 태그 목록(`tagList`)도 자유롭게 업데이트할 수 있도록 하여, 사용자 편의성과 콘텐츠 관리의 유연성을 높입니다.

## 해결 방안

### 1. OpenAPI 명세 확장 (`openapi.yml`)

-   `components.schemas.UpdateArticle` 모델의 `properties`에 `tagList` 필드를 추가합니다. 이 필드는 선택 사항(optional)이어야 합니다.

    ```yaml
    # components.schemas.UpdateArticle
    UpdateArticle:
      type: object
      properties:
        title:
          type: string
        description:
          type: string
        body:
          type: string
        tagList:      # <-- 이 부분을 추가
          type: array
          items:
            type: string
    ```

### 2. 서비스 로직 수정 (`ArticleService.java`)

-   `updateArticle` 메소드 내부 로직을 수정합니다.
    -   요청 객체(`UpdateArticleRequest`)에 `tagList`가 포함되어 있는지 확인합니다.
    -   `tagList`가 존재할 경우, `createArticle`에서 사용했던 `processTags` 헬퍼 메소드를 재사용하여 `Article` 엔티티의 `tags` 관계를 업데이트합니다.
        -   기존 태그는 모두 지우고 새로운 태그 목록으로 덮어쓰는 방식으로 구현할 수 있습니다.

### 3. 테스트 코드 추가 (`ArticlesControllerTest.java`)

-   게시글 수정 시 태그가 성공적으로 변경되는지 검증하는 테스트 케이스를 추가합니다.
    -   기존 태그가 새 태그로 완전히 교체되는 경우
    -   태그가 새로 추가되는 경우
    -   기존 태그가 삭제되는 경우
    -   `tagList`를 빈 배열로 보내 모든 태그가 삭제되는 경우

이 개선을 통해 Realworld 표준 명세를 넘어서, 사용자에게 더 완전하고 편리한 콘텐츠 관리 경험을 제공할 수 있습니다.
