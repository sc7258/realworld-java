# 테스트 시나리오 (Test Scenarios)

## 1. 프로필 조회 (`GET /api/profiles/{username}`)

-   **[성공] 시나리오 1: 인증되지 않은 사용자가 프로필 조회**
    -   **Given**: 사용자 `userA`가 존재한다.
    -   **When**: 인증 없이 `userA`의 프로필을 조회 요청한다.
    -   **Then**: 200 OK 응답을 받는다.
    -   **And**: 응답 본문에는 `userA`의 `username`, `bio`, `image` 정보가 포함된다.
    -   **And**: `following` 필드는 `false`이다.

-   **[성공] 시나리오 2: 인증된 사용자가 다른 사용자의 프로필 조회 (팔로우하지 않는 경우)**
    -   **Given**: 사용자 `userA`와 `userB`가 존재한다.
    -   **And**: `userB`는 `userA`를 팔로우하지 않는다.
    -   **When**: `userB`로 인증하여 `userA`의 프로필을 조회 요청한다.
    -   **Then**: 200 OK 응답을 받는다.
    -   **And**: 응답 본문에는 `userA`의 `username`, `bio`, `image` 정보가 포함된다.
    -   **And**: `following` 필드는 `false`이다.

-   **[성공] 시나리오 3: 인증된 사용자가 다른 사용자의 프로필 조회 (팔로우하는 경우)**
    -   **Given**: 사용자 `userA`와 `userB`가 존재한다.
    -   **And**: `userB`는 `userA`를 팔로우하고 있다.
    -   **When**: `userB`로 인증하여 `userA`의 프로필을 조회 요청한다.
    -   **Then**: 200 OK 응답을 받는다.
    -   **And**: 응답 본문에는 `userA`의 `username`, `bio`, `image` 정보가 포함된다.
    -   **And**: `following` 필드는 `true`이다.

-   **[실패] 시나리오 4: 존재하지 않는 사용자의 프로필 조회**
    -   **When**: 존재하지 않는 `nonexistentuser`의 프로필을 조회 요청한다.
    -   **Then**: 404 Not Found 응답을 받는다.

## 2. 사용자 팔로우 (`POST /api/profiles/{username}/follow`)

-   **[성공] 시나리오 1: 사용자를 성공적으로 팔로우**
    -   **Given**: 사용자 `userA`와 `userB`가 존재한다.
    -   **And**: `userB`는 `userA`를 팔로우하지 않는다.
    -   **When**: `userB`로 인증하여 `userA`를 팔로우 요청한다.
    -   **Then**: 200 OK 응답을 받는다.
    -   **And**: 응답 본문에는 `userA`의 프로필 정보가 포함된다.
    -   **And**: `following` 필드는 `true`이다.

-   **[실패] 시나리오 2: 인증 없이 팔로우 시도**
    -   **Given**: 사용자 `userA`가 존재한다.
    -   **When**: 인증 없이 `userA`를 팔로우 요청한다.
    -   **Then**: 401 Unauthorized 응답을 받는다.

-   **[실패] 시나리오 3: 존재하지 않는 사용자 팔로우 시도**
    -   **Given**: 사용자 `userB`가 존재한다.
    -   **When**: `userB`로 인증하여 존재하지 않는 `nonexistentuser`를 팔로우 요청한다.
    -   **Then**: 404 Not Found 응답을 받는다.

-   **[실패] 시나리오 4: 자기 자신을 팔로우 시도**
    -   **Given**: 사용자 `userA`가 존재한다.
    -   **When**: `userA`로 인증하여 자기 자신을 팔로우 요청한다.
    -   **Then**: 422 Unprocessable Entity 또는 400 Bad Request 응답을 받는다. (서버 정책에 따라)

-   **[실패] 시나리오 5: 이미 팔로우하고 있는 사용자를 다시 팔로우 시도**
    -   **Given**: 사용자 `userA`와 `userB`가 존재한다.
    -   **And**: `userB`는 `userA`를 이미 팔로우하고 있다.
    -   **When**: `userB`로 인증하여 `userA`를 다시 팔로우 요청한다.
    -   **Then**: 422 Unprocessable Entity 또는 200 OK 응답을 받되, 아무 변화가 없다. (멱등성)

## 3. 사용자 언팔로우 (`DELETE /api/profiles/{username}/follow`)

-   **[성공] 시나리오 1: 사용자를 성공적으로 언팔로우**
    -   **Given**: 사용자 `userA`와 `userB`가 존재한다.
    -   **And**: `userB`는 `userA`를 팔로우하고 있다.
    -   **When**: `userB`로 인증하여 `userA`를 언팔로우 요청한다.
    -   **Then**: 200 OK 응답을 받는다.
    -   **And**: 응답 본문에는 `userA`의 프로필 정보가 포함된다.
    -   **And**: `following` 필드는 `false`이다.

-   **[실패] 시나리오 2: 인증 없이 언팔로우 시도**
    -   **Given**: 사용자 `userA`가 존재한다.
    -   **When**: 인증 없이 `userA`를 언팔로우 요청한다.
    -   **Then**: 401 Unauthorized 응답을 받는다.

-   **[실패] 시나리오 3: 존재하지 않는 사용자 언팔로우 시도**
    -   **Given**: 사용자 `userB`가 존재한다.
    -   **When**: `userB`로 인증하여 존재하지 않는 `nonexistentuser`를 언팔로우 요청한다.
    -   **Then**: 404 Not Found 응답을 받는다.

-   **[실패] 시나리오 4: 자기 자신을 언팔로우 시도**
    -   **Given**: 사용자 `userA`가 존재한다.
    -   **When**: `userA`로 인증하여 자기 자신을 언팔로우 요청한다.
    -   **Then**: 422 Unprocessable Entity 또는 400 Bad Request 응답을 받는다.

-   **[성공] 시나리오 5: 팔로우하지 않는 사용자를 언팔로우 시도**
    -   **Given**: 사용자 `userA`와 `userB`가 존재한다.
    -   **And**: `userB`는 `userA`를 팔로우하지 않는다.
    -   **When**: `userB`로 인증하여 `userA`를 언팔로우 요청한다.
    -   **Then**: 200 OK 응답을 받는다. (멱등성)
    -   **And**: `following` 필드는 `false`이다.
