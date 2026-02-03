# AI 에이전트 개발 가이드 (realworld-java)

이 문서는 AI 에이전트가 **realworld-java** 프로젝트 개발을 효과적으로 지원하기 위한 지침을 제공합니다.

## 1. 기본 원칙 (Core Principles)

- **모범 사례 준수**: 솔루션을 제공할 때는 항상 현대적인 Java 및 Spring Boot 개발 모범 사례를 따라야 합니다.
    - 깨끗하고 유지보수 가능하며 잘 문서화된 코드 작성.
    - 논리적이고 확장 가능한 프로젝트 구조 보장 (예: 기능별 패키지 구조).
    - 빌드 및 테스트 자동화를 위해 Gradle과 JUnit을 적극적으로 활용.
    - 사용 중인 기술(Java, Spring Boot, Gradle, JPA 등)에 대한 확립된 규칙 준수.

- **테스트 주도 개발 (TDD) 지향**: 기능 개발 및 리팩토링은 항상 테스트 코드의 성공을 최종 목표로 합니다.
    - **Red-Green-Refactor**: 실패하는 테스트(Red)를 확인하고, 테스트를 통과하는 최소한의 코드(Green)를 작성한 후, 코드를 리팩토링(Refactor)하는 사이클을 지향합니다.
    - **JUnit & MockMvc 활용**: 프로젝트에 설정된 `JUnit5`, `SpringBootTest`, `MockMvc`를 사용하여 단위 테스트 및 통합 테스트를 작성합니다.

- **간결하고 유용한 응답**: 답변은 직접적이고 이해하기 쉬워야 하며, 사용자의 요청을 효율적으로 해결하는 데 중점을 두어야 합니다.

- **한국어 응답 (Korean Responses)**: 사용자와의 모든 상호작용은 명확하고 자연스러운 한국어를 사용해야 합니다. 기술 용어는 원어를 병기할 수 있습니다.

- **일관성 유지**: 새로운 코드나 구성이 기존 프로젝트의 스타일 및 아키텍처와 일관성을 유지하도록 해야 합니다. `README.md`에 명시된 프로젝트 아키텍처를 반드시 따릅니다.

- **가설 기반의 문제 해결**: 문제 발생 시, 성급하게 결론 내리지 않습니다.
    - **가설 설정**: 문제의 원인에 대한 여러 가설을 세웁니다. (예: "생성기가 파일을 안 만든다", "생성된 파일 이름이 다르다", "빌드 설정이 잘못되었다")
    - **최소 단위 검증**: 가장 간단한 방법으로 가설을 검증합니다. (예: `ls` 또는 `find` 명령으로 생성된 파일의 실제 존재 여부와 이름을 확인)
    - **점진적 수정**: 검증된 사실을 바탕으로 코드를 수정합니다.

- **리팩토링 파급 효과 전파**: 클래스를 새로 생성하거나 다른 패키지로 이동할 때, 해당 변경 사항이 프로젝트 전체에 미치는 영향을 반드시 추적하고 전파해야 합니다.
    - **`import` 문 확인**: 클래스를 생성/이동한 후에는, 해당 클래스를 사용하는 **모든 파일**(`src/main` 및 `src/test` 포함)의 `import` 문이 올바르게 수정되었는지 반드시 확인합니다.
    - **오류의 첫 번째 원인**: `cannot find symbol` 컴파일 오류가 발생하면, 복잡한 원인을 추측하기 전에 가장 먼저 관련 클래스의 `import` 문이 누락되거나 잘못되지 않았는지부터 의심하고 확인합니다.

---

## 2. OpenAPI Generator 연동 특별 지침

이 프로젝트는 **OpenAPI Generator를 활용한 계약 우선 개발**을 채택하고 있습니다. 이와 관련된 문제를 해결할 때는 다음 지침을 반드시 따릅니다.

- **`README.md`를 신뢰**: `README.md`의 "프로젝트 구조 및 OpenAPI 연동 전략" 섹션은 여러 시행착오 끝에 확립된 최종 아키텍처입니다. **반드시 이 문서를 먼저 읽고 제안된 아키텍처를 존중해야 합니다.**

- **생성된 파일의 존재와 이름을 먼저 확인**:
    - 컴파일 오류(`cannot find symbol`) 발생 시, "생성기가 파일을 안 만든다"고 단정하지 않습니다.
    - `build/generated/openapi` 디렉터리를 탐색하여 **실제로 생성된 파일의 정확한 이름과 패키지 경로**를 확인하는 것이 최우선입니다.

- **OpenAPI Generator의 동작 방식 이해**:
    - **요청/응답 클래스 이름**: `openapi.yml`의 `components` 섹션(`requestBodies`, `responses`)에 정의된 이름을 그대로 사용합니다. (예: `NewUserRequest` -> `NewUserRequest.java`, `UserResponse` -> `UserResponse.java`)
    - **이름 충돌 문제**: API 모델(DTO)과 JPA 엔티티의 이름이 같을 경우(예: `User`), 패키지를 분리하여(`users/model`과 `users/entity`) 충돌을 해결하는 것이 이 프로젝트의 핵심 아키텍처입니다. 이 구조를 절대 임의로 변경해서는 안 됩니다.

- **`build.gradle`의 핵심 옵션을 존중**:
    - `apiPackage`, `modelPackage`: 생성된 코드의 패키지 경로를 지정하여 이름 충돌을 방지하는 중요한 설정입니다.
    - `interfaceOnly: "true"`: 컨트롤러의 실제 구현 로직을 생성하지 않고, API의 '계약'인 인터페이스만 생성하도록 합니다.

---

## 3. 컨트롤러 구현 특별 지침 (Controller Implementation Guidelines)

### 문제 상황: API 인터페이스와 인증 정보의 충돌

컨트롤러 메소드에서 `@AuthenticationPrincipal`을 사용하여 인증된 사용자 정보를 파라미터로 주입받고 싶을 때가 있습니다. 하지만, 컨트롤러가 구현하는 생성된 API 인터페이스(`...Api.java`)의 메소드 시그니처에는 이 파라미터가 존재하지 않아 컴파일 오류가 발생합니다.

### 잘못된 해결책 (절대 사용 금지)

1.  **인터페이스 구현(`implements`) 포기**: 컴파일 오류를 피하기 위해 `implements ProfileApi` 구문을 제거하는 것은, 이 프로젝트의 핵심인 **계약 우선 개발 원칙을 위배**하는 행위이므로 절대 해서는 안 됩니다.
2.  **API 인터페이스 직접 수정**: `build/generated` 폴더의 생성된 코드를 직접 수정하는 것은, 빌드할 때마다 변경 사항이 사라지므로 의미가 없습니다.

### 올바른 해결책: `SecurityContextHolder` 사용

**컨트롤러는 반드시 생성된 API 인터페이스를 구현해야 합니다.** 메소드 시그니처는 인터페이스의 것을 그대로 따라야 합니다.

인증된 사용자 정보가 필요할 경우, 파라미터로 주입받는 대신 **메소드 내부에서 `SecurityContextHolder`를 통해 직접 접근**해야 합니다.

**올바른 코드 예시 (`ProfilesController.java`):**

```java
@RestController
@RequestMapping("/api")
public class ProfilesController implements ProfileApi {

    // ... 생성자 주입 ...

    @Override
    public ResponseEntity<ProfileResponse> getProfileByUsername(String username) {
        // 메소드 시그니처는 인터페이스와 동일하게 유지한다.
        // @AuthenticationPrincipal을 사용하지 않는다.

        // 필요한 인증 정보는 SecurityContextHolder를 통해 내부에서 직접 가져온다.
        User currentUser = getCurrentUserFromSecurityContext();
        
        ProfileResponse profileResponse = profileService.getProfile(username, currentUser);
        return ResponseEntity.ok(profileResponse);
    }

    private User getCurrentUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        // ... 사용자 정보 조회 로직 ...
    }
}
```

> **결론**: API 인터페이스의 시그니처를 절대 변경하지 마십시오. 인증 정보가 필요하면, 메소드 내부에서 `SecurityContextHolder`를 통해 접근하십시오.

---

## 4. 온보딩 프로세스 (Onboarding Process)

새로운 채팅 세션이 시작될 때, AI 에이전트는 프로젝트의 맥락을 파악하기 위해 다음 단계를 반드시 수행해야 합니다.

1.  **`README.md` 파일 정독**: 프로젝트의 개요, 목표, 아키텍처, OpenAPI 연동 전략을 파악합니다.
2.  **`AGENTS.md` 파일 정독 (이 문서)**: AI 에이전트로서 따라야 할 구체적인 지침과 원칙을 숙지합니다.
3.  **`build.gradle` 파일 검토**: `openApiGenerate` 설정을 포함한 프로젝트의 의존성과 빌드 로직을 확인합니다.

---

## 5. 작업 관리 프로세스 (Issue Management Process)

이 프로젝트는 `works/issues` 디렉토리를 통해 작업을 관리합니다. 모든 작업은 다음의 명확한 절차를 따라야 합니다.

1.  **이슈 생성 (Issue Creation)**:
    -   사용자로부터 새로운 기능 구현이나 버그 수정 요청을 받으면, 먼저 `works/issues/open` 디렉토리에 해당 작업의 이름으로 새 폴더를 생성합니다. (예: `works/issues/open/04-comments`)
    -   이 폴더 안에는 최소한 다음 세 개의 파일을 생성해야 합니다.
        -   `README.md`: 작업의 목표와 개요를 기술합니다.
        -   `requirements.md`: 구체적인 요구사항과 구현 항목을 상세히 정의합니다.
        -   `test-scenarios.md`: 성공 및 실패 케이스를 포함한 테스트 시나리오를 정의합니다.
    -   **주의**: 이 단계에서는 실제 코드 구현을 시작하지 않습니다.

2.  **작업 시작 (Start Progress)**:
    -   사용자가 "계속해줘" 또는 "작업을 시작해줘"와 같이 명시적으로 작업 시작을 지시하면, `open` 디렉토리에 있던 작업 폴더를 `works/issues/in-progress` 디렉토리로 이동시킵니다.
    -   이동이 완료된 후, `in-progress` 폴더 내의 문서를 바탕으로 실제 코드 개발(예: `openapi.yml` 수정, 소스 코드 작성, 테스트 코드 작성)을 시작합니다.

3.  **작업 완료 (Completion)**:
    -   구현과 테스트가 모두 완료되면, 작업 폴더를 `works/issues/done`으로 이동시켜 작업을 마무합니다.

이 프로세스를 통해 작업의 상태(`open`, `in-progress`, `done`)를 명확하게 추적하고, 성급한 구현을 방지합니다.
