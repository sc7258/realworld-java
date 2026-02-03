# 2. DTO 변환 로직 분리: MapStruct 도입

## 현황
`CommentService`와 같은 서비스 클래스 내부에 `mapToCommentDto`와 같은 private 메서드를 두어, JPA 엔티티를 API 모델(DTO)로 직접 변환하고 있습니다.

## 문제점
- **관심사 분리 원칙 위배**: 서비스 클래스가 비즈니스 로직 처리와 데이터 변환이라는 두 가지 책임을 갖게 됩니다.
- **가독성 및 재사용성 저하**: 변환 로직이 복잡해질수록 서비스 코드가 비대해지고, 다른 곳에서 동일한 변환 로직을 재사용하기 어렵습니다.

## 개선 방안
**MapStruct** 라이브러리를 도입하여 엔티티와 DTO 간의 변환을 담당하는 매퍼(Mapper) 인터페이스를 정의합니다. MapStruct는 컴파일 시점에 해당 인터페이스의 구현체를 자동으로 생성해주므로, 개발자는 반복적인 변환 코드를 작성할 필요가 없습니다.

## 기대 효과
- **관심사 분리**: 변환 로직이 매퍼 계층으로 완벽하게 분리됩니다.
- **코드 간결화**: 반복적인 `getter/setter` 호출 코드가 사라져 서비스 로직의 가독성이 향상됩니다.
- **성능**: 컴파일 시점에 코드를 생성하므로 리플렉션 기반 라이브러리에 비해 성능 저하가 없습니다.

## 코드 예시
**매퍼 인터페이스 정의 (`CommentMapper.java`)**
```java
@Mapper(componentModel = "spring", uses = ProfileMapper.class) // 다른 매퍼와 조합 가능
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    com.sc7258.realworldjava.model.Comment toDto(com.sc7258.realworldjava.comments.entity.Comment comment);
}
```

**서비스에서 사용 (`CommentService.java`)**
```java
// ...
private final CommentMapper commentMapper; // 생성자 주입

// ...
return commentMapper.toDto(savedComment); // 메서드 호출 한 번으로 변환 완료
```