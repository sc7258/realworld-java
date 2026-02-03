# 테스트 시나리오

### 1. 사용자 등록 (`POST /api/users`)
- **성공**
  - [x] 유효한 `username`, `email`, `password`로 사용자 등록 시, 201 Created와 함께 `UserResponse` (JWT 포함) 반환
- **실패**
  - [x] 필수 필드(username, email, password) 누락 시, 422 Unprocessable Entity 반환
  - [x] 이미 존재하는 `email`로 등록 시, 422 Unprocessable Entity 반환
  - [x] 이미 존재하는 `username`으로 등록 시, 422 Unprocessable Entity 반환
  - [x] 유효하지 않은 이메일 형식 사용 시, 422 Unprocessable Entity 반환

### 2. 사용자 로그인 (`POST /api/users/login`)
- **성공**
  - [x] 올바른 `email`과 `password`로 로그인 시, 200 OK와 함께 `UserResponse` (JWT 포함) 반환
- **실패**
  - [x] 필수 필드(email, password) 누락 시, 422 Unprocessable Entity 반환
  - [x] 존재하지 않는 `email`로 로그인 시, 401 Unauthorized 반환
  - [x] 잘못된 `password`로 로그인 시, 401 Unauthorized 반환

### 3. 현재 사용자 정보 조회 (`GET /api/user`)
- **성공**
  - [x] 유효한 JWT와 함께 요청 시, 200 OK와 함께 현재 사용자의 `UserResponse` 반환
- **실패**
  - [x] JWT 없이 요청 시, 401 Unauthorized 반환
  - [x] 유효하지 않은/만료된 JWT로 요청 시, 401 Unauthorized 반환

### 4. 사용자 정보 수정 (`PUT /api/user`)
- **성공**
  - [x] 유효한 JWT와 함께 `email`, `username`, `password`, `bio`, `image` 중 일부 또는 전체를 수정 요청 시, 200 OK와 함께 업데이트된 `UserResponse` 반환
- **실패**
  - [x] JWT 없이 요청 시, 401 Unauthorized 반환
  - [x] 유효하지 않은/만료된 JWT로 요청 시, 401 Unauthorized 반환
  - [x] 이미 존재하는 `email`로 변경 요청 시, 422 Unprocessable Entity 반환
  - [x] 이미 존재하는 `username`으로 변경 요청 시, 422 Unprocessable Entity 반환
