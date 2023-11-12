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
- 이 코드의 설계상 문제점은 무엇일까?
- 다른 저장소로 변경할 때 OCP 원칙을 잘 준수할까?
- DIP를 잘 지키고 있을까?
- **의존관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제점이 있음**
- **주문까지 만들고나서 문제점과 해결 방안을 설명


# 5. 주문과 할인 도메인 설계

## 주문과 할인 정책
- 회원은 상품을 주문할 수 있다.
- 회원 등급에 따라 할인 정책을 적용할 수 있다.
- 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
- 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)


주문 도메인 협력, 역할, 책임

![주문](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/ededa4be-8053-495d-ad41-64575a4bbc39)

**1. 주문 생성:** 클라이언트는 주문 서비스에 주문 생성을 요청한다.
**2. 회원 조회:** 할인을 위해서는 회원 등급이 필요하다. 그래서 주문 서비스는 회원 저장소에서 회원을 조회한다.
**3. 할인 적용:** 주문 서비스는 회원 등급에 따른 할인 여부를 할인 정책에 위임한다.
**4. 주문 결과 반환:** 주문 서비스는 할인 결과를 포함한 주문 결과를 반환한다.

주문 도메인 전체

![주문 도메인 전체](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/3c63fe77-5edc-4387-8cd1-e21305e559f4)

역할과 구현을 분리해서 자유롭게 구현 객체를 조립할 수 있게 설계. 
덕분에 회원 저장소는 물론이고, 할인 정책도 유연하게 변경할 수 있다.

주문 도메인 클래스 다이어그램

![주문 도메인 클래스 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/2982bcce-00e3-4d6e-847f-1ab88b9cec0b)
회원을 메모리에서 조회하고, 정액 할인 정책(고정 금액)을 지원해도 주문 서비스를 변경하지 않아도 된다.
역할들의 협력 관계를 그대로 재사용 할 수 있다.

주문 도메인 객체 다이어그램

![주문 도메인 객체 다이어그램1](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/d9edceed-6587-422d-9882-d227fd6423fb)

![주문 도메인 객체 다이어그램2](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/0c6bb994-5934-4acb-833f-4a8a4d9b5ca7)
  
회원을 메모리가 아닌 실제 DB에서 조회하고, 정률 할인 정책(주문 금액에 따라 % 할인)을 지원해도 주문 서비스를 변
경하지 않아도 된다. 협력 관계를 그대로 재사용 할 수 있다.


# 6. 주문과 할인 도메인 개발

### 할인 정책 인터페이스

`DiscountPolicy`

```groovy
package hello.core.discount;
import hello.core.member.Member;
public interface DiscountPolicy {
/**
* @return 할인 대상 금액
*/
int discount(Member member, int price);
}
```

### 정액 할인 정책 구현체 

`FixDiscountPolicy`

```groovy
package hello.core.discount;
import hello.core.member.Grade;
import hello.core.member.Member;

public class FixDiscountPolicy implements DiscountPolicy {

    private int discountFixAmount = 1000;

    //VIP이면 1000원 할인, 아니면 할인 없음
    @Override
    public int discount(Member member, int price) {
        if(member.getGrade()== Grade.VIP){
            return discountFixAmount;
        }else {
            return 0;
        }
    }
}
```

### 주문 엔티티

`Order Class`

```groovy
package hello.core.order;

public class Order {
    private Long memberId;
    private String itemName;
    private int itemPrice;
    private int discountPrice;

    //1. 생성자
    public Order(Long memberId, String itemName, int itemPrice, int discountPrice) {
        this.memberId = memberId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.discountPrice = discountPrice;
    }

    //할인된 금액
    public int calculatePrice(){
        return itemPrice - discountPrice;
    }

    //2. Getter, Setter
    public Long getMemberId() {
        return memberId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    //Generate -> toString(출력 시 편하게 볼 수 있음)
    @Override
    public String toString() {
        return "Order{" +
                "memberId=" + memberId +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                ", discountPrice=" + discountPrice +
                '}';
    }
}
```

### 주문 서비스 인터페이스

`OrderService`

```groovy
package hello.core.order;
public interface OrderService {
Order createOrder(Long memberId, String itemName, int itemPrice);
}
```

### 주문 서비스 인터페이스

`OrderServiceImpl`

```groovy
package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

private final MemberRepository memberRepository = new
MemoryMemberRepository();
private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

@Override
public Order createOrder(Long memberId, String itemName, int itemPrice) {
Member member = memberRepository.findById(memberId);
int discountPrice = discountPolicy.discount(member, itemPrice);
return new Order(memberId, itemName, itemPrice, discountPrice);
}
}
```


# 7. 주문과 할인 실행 & 테스트

## 주문과 할인 정책 실행

`OrderApp`

```groovy
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class OrderApp {
public static void main(String[] args) {
MemberService memberService = new MemberServiceImpl();
OrderService orderService = new OrderServiceImpl();
long memberId = 1L;
Member member = new Member(memberId, "memberA", Grade.VIP);
memberService.join(member);
Order order = orderService.createOrder(memberId, "itemA", 10000);
System.out.println("order = " + order);
}
}
```

**결과**

```groovy
order = Order{memberId=1, itemName='itemA', itemPrice=10000, discountPrice=1000}
```


## 주문과 할인 정책 실행 Junit Test

`OrderServiceTest`

```groovy
package hello.core.order;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

MemberService memberService = new MemberServiceImpl();
OrderService orderService = new OrderServiceImpl();

@Test
void createOrder() {
long memberId = 1L;
Member member = new Member(memberId, "memberA", Grade.VIP);
memberService.join(member);
Order order = orderService.createOrder(memberId, "itemA", 10000);
Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
}
}
```




