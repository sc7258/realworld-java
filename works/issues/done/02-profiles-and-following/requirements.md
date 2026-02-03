# 요구사항 (Requirements)

## 1. 데이터 모델

-   [x] `Follow` 엔티티를 생성한다.
    -   `id`: 기본 키 (Long)
    -   `follower`: 팔로우를 하는 사용자 (`User` 엔티티, ManyToOne)
    -   `followed`: 팔로우를 당하는 사용자 (`User` 엔티티, ManyToOne)
    -   `follower`와 `followed`의 조합은 유니크(unique)해야 한다.
-   [x] `FollowRepository`를 생성한다.
    -   `follower`와 `followed`로 `Follow` 관계를 찾는 메소드를 포함해야 한다.
    -   특정 사용자가 팔로우하는 사용자 수를 세는 메소드를 포함할 수 있다.
    -   특정 사용자를 팔로우하는 사용자 수를 세는 메소드를 포함할 수 있다.
-   [x] `User` 엔티티에 `following`과 `followers` 관계를 추가한다.
    -   `@OneToMany` 어노테이션을 사용하여 `Follow` 엔티티와 연결한다.
    -   `following`: 내가 팔로우하는 관계 목록 (`Set<Follow>`)
    -   `followers`: 나를 팔로우하는 관계 목록 (`Set<Follow>`)

## 2. API 모델 (DTO)

-   [x] `openapi.yml`에 `Profile` 모델을 정의한다.
    -   `username`: String
    -   `bio`: String
    -   `image`: String
    -   `following`: boolean
-   [x] `openapi.yml`에 `ProfileResponse` 모델을 정의한다.
    -   `profile`: `Profile` 객체를 포함한다.

## 3. 비즈니스 로직 (Service)

-   [x] `ProfileService` 클래스를 생성한다.
-   [x] **프로필 조회 로직** (`getProfile(username, currentUser)`)
    -   `username`으로 대상 사용자를 조회한다.
    -   `currentUser`가 존재할 경우, `currentUser`가 대상 사용자를 팔로우하고 있는지 확인하여 `following` 필드를 설정한다.
    -   조회된 사용자 정보와 `following` 상태를 `Profile` DTO로 변환하여 반환한다.
-   [x] **팔로우 로직** (`followUser(username, follower)`)
    -   `username`으로 팔로우할 대상 사용자를 조회한다.
    -   `follower`와 대상 사용자 간에 이미 `Follow` 관계가 있는지 확인한다.
    -   관계가 없다면 새로운 `Follow` 엔티티를 생성하고 저장한다.
    -   팔로우 후의 프로필 정보를 반환한다.
-   [x] **언팔로우 로직** (`unfollowUser(username, follower)`)
    -   `username`으로 언팔로우할 대상 사용자를 조회한다.
    -   `follower`와 대상 사용자 간의 `Follow` 관계를 조회한다.
    -   관계가 존재하면 해당 `Follow` 엔티티를 삭제한다.
    -   언팔로우 후의 프로필 정보를 반환한다.

## 4. API 계층 (Controller)

-   [x] `ProfilesController` 클래스를 생성한다.
-   [x] `GET /api/profiles/{username}` 엔드포인트를 구현한다.
    -   `ProfileService.getProfile()`을 호출하여 결과를 반환한다.
    -   인증은 선택 사항(optional)이다.
-   [x] `POST /api/profiles/{username}/follow` 엔드포인트를 구현한다.
    -   인증이 필요하다.
    -   `ProfileService.followUser()`를 호출하여 결과를 반환한다.
-   [x] `DELETE /api/profiles/{username}/follow` 엔드포인트를 구현한다.
    -   인증이 필요하다.
    -   `ProfileService.unfollowUser()`를 호출하여 결과를 반환한다.
