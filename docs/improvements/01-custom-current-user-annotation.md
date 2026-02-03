# 1. 중복 코드 제거: `@CurrentUser` 커스텀 어노테이션 도입

## 현황
`ProfilesController`, `CommentsController` 등 여러 컨트롤러에서 인증된 사용자 정보를 가져오기 위해 `getCurrentUserFromSecurityContext()`라는 동일한 private 메서드를 중복으로 구현하여 사용하고 있습니다.

## 문제점
- **코드 중복**: 새로운 컨트롤러가 추가될 때마다 동일한 코드를 복사-붙여넣기 해야 합니다.
- **유지보수 어려움**: 사용자 정보를 가져오는 로직이 변경될 경우, 관련된 모든 컨트롤러를 찾아 수정해야 하는 번거로움이 있습니다.

## 개선 방안
Spring의 `HandlerMethodArgumentResolver`를 사용하여 `@CurrentUser`라는 커스텀 어노테이션을 만듭니다. 이 Argument Resolver는 SecurityContext에서 사용자 정보를 조회하여, 어노테이션이 붙은 파라미터에 `User` 객체를 자동으로 주입해주는 역할을 합니다.

## 기대 효과
- 컨트롤러 코드가 매우 간결하고 직관적으로 변합니다.
- 인증 사용자 조회 로직이 한 곳으로 중앙화되어 유지보수가 용이해집니다.

## 코드 예시
**수정 전 (`CommentsController`)**
```java
@Override
public ResponseEntity<SingleCommentResponse> createArticleComment(String slug, @Valid NewCommentRequest comment) {
    User currentUser = getCurrentUserFromSecurityContext(); // 직접 조회
    // ...
}

private User getCurrentUserFromSecurityContext() {
    // ... 중복 코드 ...
}
```

**수정 후 (`CommentsController`)**
```java
@Override
public ResponseEntity<SingleCommentResponse> createArticleComment(
    String slug,
    @Valid NewCommentRequest comment,
    @CurrentUser User currentUser // 어노테이션으로 깔끔하게 주입
) {
    // currentUser가 null인 경우는 Spring Security와 Argument Resolver 단에서 처리 가능
    com.sc7258.realworldjava.model.Comment commentDto = commentService.addComment(slug, comment.getComment(), currentUser);
    // ...
}
```