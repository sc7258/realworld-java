# 4. Slug 고유성 보장 로직 추가

## 현상 및 문제점

현재 `ArticleService`에서 게시글의 `slug`를 생성하는 로직은 단순히 제목(title)을 소문자로 변환하고 일부 특수문자를 하이픈(-)으로 치환하는 방식입니다.

```java
// 현재 로직
private String toSlug(String title) {
    return title.toLowerCase().replaceAll("[\\&|\\/|\\s|\\,]", "-");
}
```

이 방식은 **`slug`의 고유성을 보장하지 못하는 심각한 문제**를 가지고 있습니다. 만약 서로 다른 게시글이 같은 제목을 가질 경우, 동일한 `slug`가 생성됩니다. `Article` 엔티티의 `slug` 필드에는 `unique = true` 제약 조건이 설정되어 있으므로, 중복된 `slug`를 저장하려고 시도하면 데이터베이스 레벨에서 `DataIntegrityViolationException`이 발생하여 사용자에게 500 서버 오류가 노출됩니다.

## 개선 목표

사용자가 동일한 제목의 게시글을 작성하더라도, 시스템이 자동으로 `slug`의 중복을 피하고 고유한 `slug`를 생성하여 정상적으로 게시글이 저장되도록 보장해야 합니다.

## 해결 방안: 숫자 접미사 추가

`slug` 중복 문제를 해결하기 위한 가장 일반적인 방법은, 중복이 발생했을 때 `slug` 뒤에 숫자를 붙여 고유한 값을 찾는 것입니다.

### 제안 로직 (`generateUniqueSlug`)

1.  게시글 제목을 기반으로 기본 `slug`를 생성합니다. (예: `how-to-train-your-dragon`)
2.  `ArticleRepository`를 통해 해당 `slug`가 데이터베이스에 이미 존재하는지 확인합니다. (`existsBySlug(slug)`)
3.  **존재하지 않는 경우**: 생성된 `slug`를 그대로 반환합니다.
4.  **존재하는 경우**:
    -   `slug` 뒤에 `-1`을 붙여 새로운 `slug` 후보를 만듭니다. (예: `how-to-train-your-dragon-1`)
    -   이 새로운 `slug` 후보가 존재하지 않을 때까지 숫자를 1씩 증가시키며(`-2`, `-3`, ...) 데이터베이스 조회를 반복합니다.
    -   최초로 발견된 고유한 `slug`를 최종 `slug`로 반환합니다.

### 적용 위치

이 새로운 `generateUniqueSlug` 로직은 다음 두 곳에 반드시 적용되어야 합니다.

1.  **게시글 생성 시**: `ArticleService`의 `createArticle` 메소드 내부
2.  **게시글 수정 시**: `ArticleService`의 `updateArticle` 메소드 내부에서 `title`이 변경되었을 경우

이 개선을 통해 `slug`의 고유성을 보장하고, 시스템의 안정성을 높일 수 있습니다.
