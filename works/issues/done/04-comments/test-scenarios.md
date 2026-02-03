# 테스트 시나리오

## 1. 댓글 추가 (POST /api/articles/{slug}/comments)

### 성공 케이스
- **Given**: 인증된 사용자(user1)와 존재하는 게시글(article1)이 있고,
- **When**: `user1`이 `article1`에 "This is a comment." 라는 내용으로 댓글을 작성하면,
- **Then**:
    - HTTP 200 OK 응답을 받는다.
    - 응답 본문에는 생성된 댓글 정보(id, body, author 등)가 포함된다.
    - 댓글의 `author`는 `user1`의 프로필 정보와 일치한다.

### 실패 케이스
- **Given**: 인증되지 않은 사용자가,
- **When**: 존재하는 게시글에 댓글을 작성하려고 하면,
- **Then**: HTTP 401 Unauthorized 응답을 받는다.

- **Given**: 인증된 사용자가,
- **When**: 존재하지 않는 게시글(`non-existent-slug`)에 댓글을 작성하려고 하면,
- **Then**: HTTP 404 Not Found 응답을 받는다.

- **Given**: 인증된 사용자가,
- **When**: 댓글 내용(`body`) 없이 요청을 보내면,
- **Then**: HTTP 422 Unprocessable Entity 응답을 받는다.

## 2. 댓글 목록 조회 (GET /api/articles/{slug}/comments)

### 성공 케이스
- **Given**: 특정 게시글(article1)에 여러 개의 댓글(comment1, comment2)이 존재하고,
- **When**: 해당 게시글의 댓글 목록을 조회하면,
- **Then**:
    - HTTP 200 OK 응답을 받는다.
    - 응답 본문에는 해당 게시글의 모든 댓글 목록이 최신순으로 포함된다.
    - 각 댓글에는 작성자(author)의 프로필 정보가 포함된다.

- **Given**: 댓글이 없는 게시글(article2)이 존재하고,
- **When**: 해당 게시글의 댓글 목록을 조회하면,
- **Then**:
    - HTTP 200 OK 응답을 받는다.
    - 응답 본문의 `comments` 배열은 비어 있다.

### 실패 케이스
- **When**: 존재하지 않는 게시글(`non-existent-slug`)의 댓글 목록을 조회하면,
- **Then**: HTTP 404 Not Found 응답을 받는다.

## 3. 댓글 삭제 (DELETE /api/articles/{slug}/comments/{id})

### 성공 케이스
- **Given**: 인증된 사용자(user1)가 자신이 작성한 댓글(comment1)이 있는 게시글(article1)이 있고,
- **When**: `user1`이 `comment1`을 삭제하면,
- **Then**:
    - HTTP 200 OK 응답을 받는다.
    - 이후 해당 게시글의 댓글 목록을 조회했을 때 `comment1`이 포함되지 않는다.

### 실패 케이스
- **Given**: 인증되지 않은 사용자가,
- **When**: 특정 댓글을 삭제하려고 하면,
- **Then**: HTTP 401 Unauthorized 응답을 받는다.

- **Given**: 인증된 사용자(user2)가 다른 사용자(user1)가 작성한 댓글(comment1)을,
- **When**: 삭제하려고 하면,
- **Then**: HTTP 403 Forbidden 응답을 받는다.

- **Given**: 인증된 사용자가,
- **When**: 존재하지 않는 게시글의 댓글을 삭제하려고 하면,
- **Then**: HTTP 404 Not Found 응답을 받는다.

- **Given**: 인증된 사용자가,
- **When**: 존재하는 게시글에 속하지 않은 댓글(`comment-from-other-article`)을 삭제하려고 하면,
- **Then**: HTTP 404 Not Found 응답을 받는다.
