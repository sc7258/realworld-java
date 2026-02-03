# 프로젝트 개선 제안 사항

이 문서는 `realworld-java` 프로젝트의 코드 품질, 유지보수성, 확장성을 향상시키기 위한 리팩토링 아이디어 목록입니다.

각 항목에 대한 자세한 내용은 아래 링크를 참고하세요.

## 개선 항목 목록

1.  [**중복 코드 제거: `@CurrentUser` 커스텀 어노테이션 도입**](./01-custom-current-user-annotation.md)
    - 여러 컨트롤러에 중복된 `getCurrentUserFromSecurityContext()` 메서드를 제거하고, `@CurrentUser` 어노테이션을 통해 사용자 정보를 깔끔하게 주입받는 방법을 제안합니다.

2.  [**DTO 변환 로직 분리: MapStruct 도입**](./02-separate-dto-mapping-with-mapstruct.md)
    - 서비스 계층에 혼재된 엔티티-DTO 변환 로직을 MapStruct 라이브러리를 사용하여 매퍼(Mapper) 계층으로 분리하는 방법을 제안합니다.

3.  [**테스트 전략 구체화: 단위 테스트 추가**](./03-add-service-unit-tests.md)
    - 실행 속도가 느린 통합 테스트를 보완하기 위해, Mockito를 활용하여 서비스 계층의 비즈니스 로직만 고립시켜 검증하는 빠른 단위 테스트를 추가하는 방법을 제안합니다.
