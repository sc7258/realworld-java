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

## 2. 컨트롤러와 테스트의 구조적 일관성

**모든 컨트롤러는 자신만의 테스트 클래스를 가져야 합니다.** 이는 코드와 테스트의 구조적 일관성을 유지하고, 책임 소재를 명확히 하기 위함입니다.

- **1 컨트롤러 = 1 테스트 클래스**:
    - `ArticlesController`는 `ArticlesControllerTest`가 대응합니다.
    - `FavoritesController`를 새로 만들었다면, 반드시 `FavoritesControllerTest`도 함께 생성해야 합니다.

- **리팩토링 시 테스트도 함께 이전**:
    - 특정 기능(예: '좋아요')을 `ArticlesController`에서 `FavoritesController`로 이전하는 경우, 관련 테스트 코드 또한 `ArticlesControllerTest`에서 `FavoritesControllerTest`로 **반드시 함께 이전**해야 합니다.
    - 리팩토링 후, 기존 테스트 클래스(`ArticlesControllerTest`)에 더 이상 관련 없는 테스트 코드가 남아있어서는 안 됩니다.

- **항상 확인**: 새로운 컨트롤러를 추가하거나 기존 컨트롤러의 책임을 변경할 때, `src/test/java` 아래의 테스트 구조가 `src/main/java`의 구조와 일치하는지 항상 확인하고 점검해야 합니다.

---

## 3. OpenAPI Generator 연동 특별 지침

이 프로젝트는 **OpenAPI Generator를 활용한 계약 우선 개발**을 채택하고 있습니다. 이와 관련된 문제를 해결할 때는 다음 지침을 반드시 따릅니다.

- **`README.md`를 신뢰**: `README.md`의 "프로젝트 구조 및 OpenAPI 연동 전략" 섹션은 여러 시행착오 끝에 확립된 최종 아키텍처입니다. **반드시 이 문서를 먼저 읽고 제안된 아키텍처를 존중해야 합니다.**

- **생성된 파일의 존재와 이름을 먼저 확인**:
    - 컴파일 오류(`cannot find symbol`) 발생 시, "생성기가 파일을 안 만든다"고 단정하지 않습니다.
    - `build/generated/openapi` 디렉터리를 탐색하여 **실제로 생성된 파일의 정확한 이름과 패키지 경로**를 확인하는 것이 최우선입니다.

- **OpenAPI Generator의 동작 방식 이해**:
    - **`tags` 기준 인터페이스 생성**: OpenAPI Generator는 `openapi.yml`의 `paths` 아래 각 오퍼레이션에 명시된 `tags`를 기준으로 API 인터페이스 파일을 생성합니다. (예: `tags: [Articles]` -> `ArticlesApi.java`, `tags: [Favorites]` -> `FavoritesApi.java`)
    - **이름 충돌 문제**: API 모델(DTO)과 JPA 엔티티의 이름이 같을 경우(예: `User`), 패키지를 분리하여(`users/model`과 `users/entity`) 충돌을 해결하는 것이 이 프로젝트의 핵심 아키텍처입니다. 이 구조를 절대 임의로 변경해서는 안 됩니다.

- **`build.gradle`의 핵심 옵션을 존중**:
    - `apiPackage`, `modelPackage`: 생성된 코드의 패키지 경로를 지정하여 이름 충돌을 방지하는 중요한 설정입니다.
    - `interfaceOnly: "true"`: 컨트롤러의 실제 구현 로직을 생성하지 않고, API의 '계약'인 인터페이스만 생성하도록 합니다.

---

## 4. 컨트롤러 구현 특별 지침 (Controller Implementation Guidelines)

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

## 5. 온보딩 프로세스 (Onboarding Process)

새로운 채팅 세션이 시작될 때, AI 에이전트는 프로젝트의 맥락을 파악하기 위해 다음 단계를 반드시 수행해야 합니다.

1.  **`README.md` 파일 정독**: 프로젝트의 개요, 목표, 아키텍처, OpenAPI 연동 전략을 파악합니다.
2.  **`AGENTS.md` 파일 정독 (이 문서)**: AI 에이전트로서 따라야 할 구체적인 지침과 원칙을 숙지합니다.
3.  **`build.gradle` 파일 검토**: `openApiGenerate` 설정을 포함한 프로젝트의 의존성과 빌드 로직을 확인합니다.

---

## 6. 작업 관리 프로세스 (Issue Management Process)

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

### 6.1. 이슈 폴더 상태 변경 절차 (에이전트 제약사항)

AI 에이전트는 파일 시스템에서 폴더를 직접 이동하거나 삭제하는 기능이 없습니다. 따라서 이슈 폴더의 상태를 변경(`in-progress` -> `done` 등)할 때는 다음의 **우회적인 절차**를 따릅니다.

1.  **새 위치에 복사**:
    -   먼저, 이동할 대상 폴더(예: `works/issues/done/06-tags`)를 새로 만듭니다.
    -   원본 폴더(예: `works/issues/in-progress/06-tags`)에 있는 모든 파일의 내용을 하나씩 읽어, 새로운 대상 폴더에 동일한 이름의 파일로 다시 씁니다.

2.  **원본 위치에 이동 경로 명시**:
    -   복사가 완료되면, 원본 폴더에 있는 모든 파일의 내용을 **"This issue has been completed and moved to the 'done' directory."** 라는 메시지로 덮어씁니다.
    -   이를 통해 원본 폴더는 '아카이브'되었음을 명시적으로 나타냅니다.

이 절차는 물리적인 폴더 이동을 흉내 내는 것으로, 에이전트의 기능적 한계 내에서 작업 상태를 최대한 명확하게 반영하기 위한 것입니다.

### 6.2. 작업 상태 변경 명확화 및 주의사항

**가장 중요한 원칙: 작업은 반드시 `open` → `in-progress` → `done` 순서로 이동합니다.**

이전 작업에서 `open` 상태의 이슈를 `done`으로 바로 이동 처리하는 실수가 발생했습니다. 이는 프로젝트의 작업 흐름을 심각하게 왜곡하므로 절대 반복해서는 안 됩니다.

**상태 변경 시나리오별 정확한 절차:**

1.  **`open` → `in-progress` (작업 시작)**
    -   **상황**: 사용자가 `works/issues/open/XX-some-feature`에 대한 작업을 시작하라고 지시할 때.
    -   **조치**:
        -   `works/issues/in-progress/XX-some-feature` 폴더와 그 안의 파일들(`README.md` 등)을 생성합니다. (실제로는 파일 복사로 구현)
        -   `works/issues/open/XX-some-feature` 폴더의 모든 파일 내용을 **"This issue has been moved to the 'in-progress' directory."** 로 덮어씁니다.
    -   **주의**: `done` 디렉토리로 이동했다는 메시지를 절대 사용해서는 안 됩니다.

2.  **`in-progress` → `done` (작업 완료)**
    -   **상황**: `works/issues/in-progress/XX-some-feature`에 대한 모든 구현과 테스트가 완료되었을 때.
    -   **조치**:
        -   `works/issues/done/XX-some-feature` 폴더와 그 안의 파일들을 생성합니다. (실제로는 파일 복사로 구현)
        -   `works/issues/in-progress/XX-some-feature` 폴더의 모든 파일 내용을 **"This issue has been completed and moved to the 'done' directory."** 로 덮어씁니다.

**실수 사례 분석 (Case Study):**
-   **문제**: `open` 상태의 여러 이슈 파일에 대해 "completed and moved to the 'done' directory"라는 메시지를 기록함.
-   **원인**: `open` -> `in-progress` 단계를 건너뛰고, 작업 상태 흐름을 잘못 이해함.
-   **교훈**: 상태 변경 요청을 받으면, 항상 현재 이슈가 어느 디렉토리(`open` 또는 `in-progress`)에 있는지 먼저 확인하고, 그에 맞는 다음 상태(`in-progress` 또는 `done`)로만 이동을 기록해야 합니다.

---

## 7. 통합 테스트 작성 특별 지침 (`@SpringBootTest`)

`@SpringBootTest`를 사용하여 컨트롤러 통합 테스트를 작성할 때, 반복적인 오류를 피하고 안정적인 테스트를 구축하기 위해 다음 지침을 **반드시** 따릅니다.

### 문제 상황: `DataIntegrityViolationException`과 `UsernameNotFoundException`
테스트 실행 시, 특히 `@Transactional` 환경에서 다음과 같은 오류들이 빈번하게 발생할 수 있습니다.

- **`DataIntegrityViolationException`**: DB의 `UNIQUE` 또는 `NOT NULL` 제약 조건을 위반할 때 발생합니다.
    - **원인 1**: `@BeforeEach`에서 데이터를 수동으로 삭제(`deleteAll`)하고 생성하는 로직이 `@Transactional`의 롤백과 꼬여, 이전 테스트 데이터가 제대로 삭제되지 않은 상태에서 중복 데이터를 `save`하려 할 때 발생합니다.
    - **원인 2**: `@CreatedDate` 같은 Auditing 필드가 `nullable=false`인데, 테스트 환경에서 Auditing 기능이 활성화되지 않아 `null` 값으로 저장하려 할 때 발생합니다.
- **`UsernameNotFoundException`**: `@WithUserDetails` 사용 시, 테스트의 인증 컨텍스트가 생성되는 시점과 `@BeforeEach`의 데이터가 DB에 저장되는 시점이 맞지 않아 발생합니다.

### 올바른 테스트 코드 패턴
이러한 문제들을 해결하기 위한 모범 사례는 다음과 같습니다.

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // (1) 테스트 후 자동 롤백을 위해 @Transactional을 사용합니다.
class CommentsControllerTest {

    // ... (Repositories, MockMvc, ObjectMapper 등 주입) ...

    @BeforeEach
    void setUp() {
        // (2) 수동으로 DB를 삭제하는 코드를 절대 넣지 않습니다.
        // @Transactional이 각 테스트 후 DB 상태를 자동으로 롤백해줍니다.
        // commentRepository.deleteAllInBatch(); (X)
        // articleRepository.deleteAllInBatch(); (X)
        // userRepository.deleteAllInBatch();    (X)

        // (3) 영속화된 객체를 반환받아 참조 무결성을 보장합니다.
        // save() 대신 saveAndFlush()를 사용하여 DB에 즉시 반영하고, 그 반환값을 사용합니다.
        User user1 = new User("user1@example.com", "user1", "password");
        User savedUser1 = userRepository.saveAndFlush(user1);

        Article article1 = new Article("slug-1", "Title 1", "Desc", "Body", savedUser1);
        // (4) Auditing 필드 문제를 해결하기 위해 엔티티 생성자에서 시간을 직접 할당합니다.
        // article1.setCreatedAt(Instant.now()); // 또는 생성자에서 처리
        articleRepository.saveAndFlush(article1);
    }

    @Test
    // (5) DB를 조회하는 @WithUserDetails 대신, @WithMockUser를 사용합니다.
    @WithMockUser(username = "user1")
    void some_authenticated_test() throws Exception {
        // ... 테스트 로직 ...
    }
}
```

### 핵심 요약
1.  **`@Transactional` 사용**: 클래스 레벨에 `@Transactional`을 붙여 테스트의 자동 롤백을 활성화합니다.
2.  **수동 `deleteAll` 금지**: `@BeforeEach`에서 `deleteAll`이나 `deleteAllInBatch`를 호출하지 마세요. `@Transactional`이 모든 것을 처리합니다.
3.  **`saveAndFlush()`와 반환값 사용**: 데이터를 저장할 때는 `saveAndFlush()`를 사용하여 즉시 DB에 반영하고, 반환된 영속 객체를 다음 객체의 외래 키로 사용하여 참조 무결성을 지키세요.
4.  **Auditing 필드 수동 할당**: `@CreatedDate` 등이 `nullable=false`인 경우, 테스트 시에는 엔티티 생성자나 `setter`를 통해 `Instant.now()` 등으로 값을 직접 할당하여 제약 조건 위반을 피하세요.
5.  **`@WithMockUser` 사용**: 인증이 필요한 테스트에는 DB 조회가 필요 없는 `@WithMockUser(username="...")`을 사용하여 타이밍 문제를 원천적으로 차단하세요.
