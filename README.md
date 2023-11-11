# Spring_Study

# 프로젝트 생성

## 사전 준비물

- Java 11 설치
- IDE: IntelliJ 또는 Eclipse 설치

## 스프링 부트 스타터 사이트로 이동해서 스프링 프로젝트 생성

[스프링 부트 스타터 사이트 바로가기](https://start.spring.io)

### 프로젝트 선택

- Project: **Gradle - Groovy** Project
- Spring Boot: 2.3.x
- Language: Java
- Packaging: Jar
- Java: 11

### Project Metadata

- groupId: hello
- artifactId: core
- Dependencies: 선택하지 않는다.


# 스프링 부트 3.0

스프링 부트 3.0을 선택하게 되면 다음 부분을 꼭 확인해주세요.

1. **Java 17 이상**을 사용해야 합니다.
2. javax 패키지 이름을 jakarta로 변경해야 합니다.

오라클과 자바 라이센스 문제로 모든 javax 패키지를 jakarta로 변경하기로 했습니다.

### 패키지 이름 변경 예시

- JPA 애노테이션
    - javax.persistence.Entity → jakarta.persistence.Entity
- 스프링에서 자주 사용하는 @PostConstruct 애노테이션
    - javax.annotation.PostConstruct → jakarta.annotation.PostConstruct
- 스프링에서 자주 사용하는 검증 애노테이션
    - javax.validation → jakarta.validation

스프링 부트 3.0 관련 자세한 내용은 [다음 링크](https://bit.ly/springboot3)를 확인해주세요.

### Gradle 전체 설정

`build.gradle`

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.5'
    id 'io.spring.dependency-management' version '1.1.3'
}

group = 'hello'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

# 1. 비즈니스 요구사항과 설계

## 회원

- 회원을 가입하고 조회할 수 있다.
- 회원은 일반과 VIP 두 가지 등급이 있다.
- 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)

## 주문과 할인 정책

- 회원은 상품을 주문할 수 있다.
- 회원 등급에 따라 할인 정책을 적용할 수 있다.
- 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
- 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)

요구사항을 보면 회원 데이터, 할인 정책 같은 부분은 지금 결정하기 어려운 부분이다. 
그렇다고 이런 정책이 결정될 때까지 개발을 무기한 기다릴 수도 없다. 인터페이스를 만들고 구현체를 언제든지 갈아끼울 수 있도록 설계하면 된다.

# 2. 회원 도메인 설계

## 회원 도메인 요구사항

- 회원을 가입하고 조회할 수 있다.
- 회원은 일반과 VIP 두 가지 등급이 있다.
- 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)


회원 도메인 협력 관계
  
![회원 도메인 협력 관계](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/8df4dd0f-511f-49d3-81b5-aeabad097b30)


회원 클래스 다이어그램

![회원 클래스 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/c1dfc4ed-a603-4f62-b477-520db2b88296)


회원 객체 다이어그램

![회원 객체 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/43dad64b-3b01-4fd2-b152-49fc2171fd23)



# 3. 회원 도메인 개발

## 회원 엔티티

### 회원 등급

`Grade Enum`

```groovy
package hello.core.member;
public enum Grade {
BASIC,
VIP
}
```

### 회원 엔티티  
`Member Class`

(Getter, Setter = Generate 기능을 사용하여 쉽게 생성하자)

```groovy
package hello.core.member;

public class Member {

private Long id;
private String name;
private Grade grade;

public Member(Long id, String name, Grade grade) {
this.id = id;
this.name = name;
this.grade = grade;
}

public Long getId() {
return id;
}
public void setId(Long id) {
this.id = id;
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
public Grade getGrade() {
return grade;
}
public void setGrade(Grade grade) {
this.grade = grade;
}
}
```

### 회원 저장소 인터페이스

`MemberRepository`

```groovy
public interface MemberRepository {
void save(Member member);
Member findById(Long memberId);
}
```

### 메모리 회원 저장소 구현체

`MemoryMemberRepository`

```groovy
package hello.core.member;
import java.util.HashMap;
import java.util.Map;

public class MemoryMemberRepository implements MemberRepository {

private static Map<Long, Member> store = new HashMap<>();

@Override
public void save(Member member) {
store.put(member.getId(), member);
}

@Override
public Member findById(Long memberId) {
return store.get(memberId);
}

}
```
참고: `HashMap` 은 동시성 이슈가 발생할 수 있다. 이런 경우 `ConcurrentHashMap` 을 사용하자.


## 회원 서비스

### 회원 서비스 인터페이스

`MemberService`

```groovy
package hello.core.member;
public interface MemberService {

void join(Member member);
Member findMember(Long memberId);

}
```

### 회원 서비스 구현체

`MemberServiceImpl`

```groovy
package hello.core.member;

public class MemberServiceImpl implements MemberService {

private final MemberRepository memberRepository = new MemoryMemberRepository();

public void join(Member member) {
memberRepository.save(member);
}

public Member findMember(Long memberId) {
return memberRepository.findById(memberId);
}
}
```

# 4. 회원 도메인 실행 & 테스트

## 회원 도메인

### 회원 도메인- 회원 가입 main 

`MemberApp`

```groovy
package hello.core;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;

public class MemberApp {
public static void main(String[] args) {

MemberService memberService = new MemberServiceImpl();

Member member = new Member(1L, "memberA", Grade.VIP);

memberService.join(member);
Member findMember = memberService.findMember(1L);
System.out.println("new member = " + member.getName());
System.out.println("find Member = " + findMember.getName());
}
}
```
![main Test](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/238ab6b2-998c-445e-aae7-88abfb3f26c5)

애플리케이션 로직으로 이렇게 테스트 하는 것은 좋은 방법이 아니다. `JUnit 테스트`를 사용하자.


### 회원 도메인 - 회원 가입 테스트
(테스트 작성 방법은 필수!!!)

`MemberServiceTest`

```groovy
package hello.core.member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

MemberService memberService = new MemberServiceImpl();

@Test
void join() {
//given
Member member = new Member(1L, "memberA", Grade.VIP);
//when
memberService.join(member);
Member findMember = memberService.findMember(1L);
//then
Assertions.assertThat(member).isEqualTo(findMember);
}
}
```


## 회원 도메인 설계의 문제점
- 이 코드의 설계상 문제점은 무엇일까요?
- 다른 저장소로 변경할 때 OCP 원칙을 잘 준수할까요?
- DIP를 잘 지키고 있을까요?
- **의존관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제점이 있음**
- **주문까지 만들고나서 문제점과 해결 방안을 설명


# 5. 주문과 할인 도메인 설계

## 주문과 할인 정책
- 회원은 상품을 주문할 수 있다.
- 회원 등급에 따라 할인 정책을 적용할 수 있다.
- 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
- 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)




  
