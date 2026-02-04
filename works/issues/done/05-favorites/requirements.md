# 요구사항: 좋아요(Favorites) API

## 1. 데이터 모델 및 Repository
- **`Favorite` 엔티티 (`Favorite.java`)**
  - `User`와 `Article`의 다대다(Many-to-Many) 관계를 표현하는 조인 테이블용 엔티티.
  - `id`: Primary Key (Long)
  - `user`: '좋아요'를 누른 사용자 (`User` 엔티티와 Many-to-One 관계)
  - `article`: '좋아요'를 받은 게시글 (`Article` 엔티티와 Many-to-One 관계)
  - `user`와 `article`의 조합은 유니크해야 함.
- **`Article` 엔티티 수정**
  - `favoritedBy` 필드 추가: `Favorite` 엔티티와 One-to-Many 관계 설정.
- **`FavoriteRepository.java`**
  - `JpaRepository`를 상속받는 인터페이스.
  - `user`와 `article`로 `Favorite` 관계를 찾는 메소드 필요 (예: `findByUserAndArticle`)

## 2. OpenAPI 명세 (`openapi.yml`) 수정
- **Paths**
  - `POST /api/articles/{slug}/favorite`: '좋아요' 추가 API 경로 (이미 존재하므로 확인)
  - `DELETE /api/articles/{slug}/favorite`: '좋아요' 취소 API 경로 (이미 존재하므로 확인)
- **Components (Schemas)**
  - `Article` 모델에 `favorited` (boolean)와 `favoritesCount` (integer) 필드가 포함되어 있는지 확인.

## 3. API 구현 (Controller, Service)
- **`ArticleService` 수정**
  - `favoriteArticle(slug, currentUser)` 메소드 추가:
    1. `slug`로 `Article` 조회 (없으면 404 Not Found).
    2. 이미 '좋아요'를 누른 상태가 아니라면, `Favorite` 엔티티를 생성하고 저장.
    3. '좋아요'가 반영된 `SingleArticleResponse` 반환.
  - `unfavoriteArticle(slug, currentUser)` 메소드 추가:
    1. `slug`로 `Article` 조회 (없으면 404 Not Found).
    2. '좋아요'를 누른 상태라면, 해당 `Favorite` 엔티티를 찾아서 삭제.
    3. '좋아요' 취소가 반영된 `SingleArticleResponse` 반환.
  - `mapToArticleModel` / `mapToArticlesInner` 메소드 수정:
    - `favorited` 필드: 현재 `currentUser`가 해당 게시글에 '좋아요'를 눌렀는지 여부를 계산하여 설정.
    - `favoritesCount` 필드: 해당 게시글의 총 '좋아요' 개수를 계산하여 설정.

- **`FavoritesController` 생성 및 구현**
  - `FavoritesApi` 인터페이스 구현
  - `createArticleFavorite` 메소드 구현:
    - `@PostMapping("/{slug}/favorite")`
    - 인증된 사용자 정보를 가져와 `articleService.favoriteArticle` 호출.
  - `deleteArticleFavorite` 메소드 구현:
    - `@DeleteMapping("/{slug}/favorite")`
    - 인증된 사용자 정보를 가져와 `articleService.unfavoriteArticle` 호출.
