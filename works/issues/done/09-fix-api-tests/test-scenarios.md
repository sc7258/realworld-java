# 테스트 시나리오

## 성공 시나리오

1.  **모든 API 테스트 통과**:
    -   **Given**: Spring Boot 애플리케이션이 `docker` 프로필로 실행 중입니다.
    -   **When**: 터미널에서 `./scripts/run-api-tests.sh` 스크립트를 실행합니다.
    -   **Then**: Newman 실행 결과, 모든 단언(assertions)이 성공하고, `failure` 카운트는 `0`이어야 합니다.
    -   **And**: 스크립트가 종료 코드 `0`으로 성공적으로 완료됩니다.

## 실패 시나리오 (현재 상태)

1.  **API 테스트 실패 발생**:
    -   **Given**: Spring Boot 애플리케이션이 `docker` 프로필로 실행 중입니다.
    -   **When**: 터미널에서 `./scripts/run-api-tests.sh` 스크립트를 실행합니다.
    -   **Then**: Newman 실행 결과, 하나 이상의 테스트에서 단언(assertion) 실패가 보고됩니다.
    -   **And**: 실패한 테스트의 상세 정보(요청, 응답, 실패 원인)가 리포트에 출력됩니다.
    -   **And**: 스크립트가 `0`이 아닌 종료 코드로 실패하며 종료됩니다.