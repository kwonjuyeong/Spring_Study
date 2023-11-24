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
</br></br>
# Chapter 1 예제

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
**단위 테스트가 정말 중요하다.**
- Spring같은 도움 없이 순수 자바 코드로 테스트를 돌림.
- 몇천개의 테스트도 몇초만에 빠르게 테스트할 수 있음.

</br></br>
# Chapter 2 객체 지향 원리 적용

# 1. 새로운 할인 정책 개발

**새로운 할인 정책을 확장해보자.**
- **악덕 기획자**: 서비스 오픈 직전에 할인 정책을 지금처럼 고정 금액 할인이 아니라 좀 더 합리적인 주문 금액당 할인하는 정률% 할인으로 변경하고 싶어요. 예를 들어서 기존 정책은 VIP가 10000원을 주문하든 20000원을 주문하든 항상 1000원을 할인했는데, 이번에 새로 나온 정책은 10%로 지정해두면 고객이 10000원 주문시 1000원을 할인해주고, 20000원 주문시에 2000원을 할인해주는 거에요!
- **순진 개발자**: 제가 처음부터 고정 금액 할인은 아니라고 했잖아요.
- **악덕 기획자**: 애자일 소프트웨어 개발 선언 몰라요? “계획을 따르기보다 변화에 대응하기를”
- **순진 개발자**: … (하지만 난 유연한 설계가 가능하도록 객체지향 설계 원칙을 준수했지 후후)

**참고**: 애자일 소프트웨어 개발 선언 https://agilemanifesto.org/iso/ko/manifesto.html

- 순진 개발자가 정말 객체지향 설계 원칙을 잘 준수 했는지 확인해보자. 이번에는 주문한 금액의 %를 할인해주는 새로운 정률 할인 정책을 추가하자.
- 우리는 객체지향 설계를 지켰기 때문에 FixDiscountPolicy(1000원 고정 할인 정책) -> RateDiscountPolicy(할인율 할인 정책)으로 바꿔주면 된다.


![RateDiscountPolicy](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/973f09e9-a8b8-4129-9b9b-eb7f2b0738a8)


## RateDiscountPolicy 코드 추가

`RateDiscountPolicy`

```groovy
package hello.core.discount;
import hello.core.member.Grade;
import hello.core.member.Member;

public class RateDiscountPolicy implements DiscountPolicy {
private int discountPercent = 10; //10% 할인

@Override
public int discount(Member member, int price) {
if (member.getGrade() == Grade.VIP) {
return price * discountPercent / 100;
} else {
return 0;
}
}

}
```

## 테스트 작성(Cntl + Shift + T 단축키)**

`RateDiscountPolicyTest`

```groovy
package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RateDiscountPolicyTest {
    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();
    @Test
    @DisplayName("VIP는 10% 할인이 적용되어야 한다.")
    void vip_o() {
    //given
    Member member = new Member(1L, "memberVIP", Grade.VIP);
    //when
    int discount = discountPolicy.discount(member, 10000);
    //then
    assertThat(discount).isEqualTo(1000);
    }
    @Test
    @DisplayName("VIP가 아니면 할인이 적용되지 않아야 한다.")
    void vip_x() {
    //given
    Member member = new Member(2L, "memberBASIC", Grade.BASIC);
    //when
    int discount = discountPolicy.discount(member, 10000);
    //then
    assertThat(discount).isEqualTo(0);
    }
}
```

# 2. 새로운 할인 정책 적용과 문제점

### 할인 정책 변경

```groovy
public class OrderServiceImpl implements OrderService {
// private final DiscountPolicy discountPolicy = new FixDiscountPolicy(); //기존 할인 정책(고정 할인)
private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); //변경 할인 정책(비율 할인)
}
```

## 문제점 발견
- 우리는 역할과 구현을 충실하게 분리했다. OK
- 다형성도 활용하고, 인터페이스와 구현 객체를 분리했다. OK
- OCP, DIP 같은 객체지향 설계 원칙을 충실히 준수했다
    - 그렇게 보이지만 사실은 아니다.
- DIP: 주문서비스 클라이언트( `OrderServiceImpl` )는 `DiscountPolicy` 인터페이스에 의존하면서 DIP를 지킨 것 같은데?
    - 클래스 의존관계를 분석해 보자. 추상(인터페이스) 뿐만 아니라 **구체(구현) 클래스에도 의존**하고 있다.
        - 추상(인터페이스) 의존: `DiscountPolicy`
        - 구체(구현) 클래스: `FixDiscountPolicy` , `RateDiscountPolicy`
- OCP: 변경하지 않고 확장할 수 있다고 했는데!
    - **지금 코드는 기능을 확장해서 변경하면, 클라이언트 코드에 영향을 준다!** 따라서 **OCP를 위반**한다.

</br></br>
## 왜? 클라이언트 코드를 변경해야 할까?

### 기대했던 의존 관계
![기대했던 의존 관계](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/26df3fe5-b9e2-4cf8-8480-a2c8a41a2804)
- 지금까지 단순히 'DiscountPolicy' 인터페이스만 의존한다고 생각했다.

### 실제 의존 관계
![실제 의존 관계](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/58707015-064f-4005-a3f0-55422c2d851e)
- 잘보면 클라이언트인 'OrderServiceImpl'이 'DiscountPolicy' 인터페이스 뿐만 아니라 'FixDiscountPolicy'인 구체 클래스도 함께 의존하고 있다. = DIP 위반

### 정책 변경
![정책 변경](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/d1a8aa83-9d69-457f-84a1-504b7d0c9f52)
- ***중요!*** : 그래서 'FixDiscountPolicy'를 'RateDiscountPolicy'로 변경하는 순간 'OrderServiceImpl'의 소스 코드도 함께 변경해야 한다. = OCP 위반 

</br></br>
## 어떻게 문제를 해결할 수 있을까?
- 클라이언트 코드인 `OrderServiceImpl` 은 `DiscountPolicy` 의 인터페이스 뿐만 아니라 구체 클래스도 함께 의존한다.
- 그래서 구체 클래스를 변경할 때 클라이언트 코드도 함께 변경해야 한다.
- **DIP 위반** 추상에만 의존하도록 변경(인터페이스에만 의존)
- DIP를 위반하지 않도록 인터페이스에만 의존하도록 의존관계를 변경하면 된다.

###**인터페이스에만 의존하도록 설계를 변경**
![image](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/2b4a5a34-406e-439b-915a-e9944698f96f)

### 인터페이스에만 의존하도록 코드 변경

```groovy
public class OrderServiceImpl implements OrderService {
//private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
private DiscountPolicy discountPolicy;
}
```
- 인터페이스에만 의존하도록 설계와 코드를 변경했다.
- **그런데 구현체가 없는데 어떻게 코드를 실행할 수 있을까?**
- 실제 실행을 해보면 NPE(null pointer exception)가 발생한다.

**해결방안**
이 문제를 해결하려면 누군가가 클라이언트인 `OrderServiceImpl` 에 `DiscountPolicy` 의 구현 객체를 대
신 생성하고 주입해주어야 한다.


# 3. 관심사의 분리

- 애플리케이션을 하나의 공연이라 생각해보자. 각각의 인터페이스를 배역(배우 역할)이라 생각하자. 그런데! 실제 배역 맞는 배우를 선택하는 것은 누가 하는가?
- 로미오와 줄리엣 공연을 하면 로미오 역할을 누가 할지 줄리엣 역할을 누가 할지는 배우들이 정하는게 아니다. 이전 코드는 마치 로미오 역할(인터페이스)을 하는 레오나르도 디카프리오(구현체, 배우)가 줄리엣 역할(인터페이스)을 하는 여자 주인공(구현체, 배우)을 직접 초빙하는 것과 같다. 디카프리오는 공연도 해야하고 동시에 여자 주인공도 공연에 직접 초빙해야 하는 **다양한 책임**을 가지고 있다.


**관심사를 분리하자**
- 배우는 본인의 역할인 배역을 수행하는 것에만 집중해야 한다.
- 디카프리오는 어떤 여자 주인공이 선택되더라도 똑같이 공연을 할 수 있어야 한다.
- 공연을 구성하고, 담당 배우를 섭외하고, 역할에 맞는 배우를 지정하는 책임을 담당하는 별도의 **공연 기획자**가 나올시점이다.
- 공연 기획자를 만들고, 배우와 공연 기획자의 책임을 확실히 분리하자.


## AppConfig 등장
- 애플리케이션의 전체 동작 방식을 구성(config)하기 위해, **구현 객체를 생성**하고, **연결**하는 책임을 가지는 별도의 설정 클래스를 만들자.

AppConfig
```groovy
package hello.core;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class AppConfig {
public MemberService memberService() {
    return new MemberServiceImpl(new MemoryMemberRepository());
}

public OrderService orderService() {
    return new OrderServiceImpl(new MemoryMemberRepository(),
    new FixDiscountPolicy());
}

}
```

- AppConfig는 애플리케이션의 실제 동작에 필요한 **구현 객체를 생성**한다.
    - `MemberServiceImpl`
    - `MemoryMemberRepository`
    - `OrderServiceImpl`
    - `FixDiscountPolicy`
- AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 **생성자를 통해서 주입(연결)**해준다.
    - `MemberServiceImpl` `MemoryMemberRepository`
    - `OrderServiceImpl` `MemoryMemberRepository` , `FixDiscountPolicy`
***참고***: 지금은 각 클래스에 생성자가 없어서 컴파일 오류가 발생한다. 바로 다음에 코드에서 생성자를 만든다.


### MemberServiceImpl - 생성자 주입

```groovy
package hello.core.member;

public class MemberServiceImpl implements MemberService {
private final MemberRepository memberRepository;
public MemberServiceImpl(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
}
public void join(Member member) {
    memberRepository.save(member);
}
public Member findMember(Long memberId) {
    return memberRepository.findById(memberId);
}
}
```

- 설계 변경으로 `MemberServiceImpl` 은 `MemoryMemberRepository` 를 의존하지 않는다!
- 단지 `MemberRepository` 인터페이스만 의존한다.
- `MemberServiceImpl` 입장에서 생성자를 통해 어떤 구현 객체가 들어올지(주입될지)는 알 수 없다.
- `MemberServiceImpl` 의 생성자를 통해서 어떤 구현 객체를 주입할지는 오직 외부( `AppConfig` )에서 결정된다.
- `MemberServiceImpl` 은 이제부터 **의존관계에 대한 고민은 외부**에 맡기고 **실행에만 집중**하면 된다.


### 클래스 다이어그램

![클래스 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/349578a6-1115-46fb-b366-3627dacc2ec2)

- 객체의 생성과 연결은 `AppConfig` 가 담당한다.
- **DIP 완성:** `MemberServiceImpl` 은 `MemberRepository` 인 추상에만 의존하면 된다. 이제 구체 클래스를 몰라도 된다.
- **관심사의 분리:** 객체를 생성하고 연결하는 역할과 실행하는 역할이 명확히 분리되었다. 

### 회원 객체 인스턴스 다이어그램
![회원 객체 인스턴스 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/76462021-e53b-436e-9aaa-d55823b95b7c)

- `appConfig` 객체는 `memoryMemberRepository` 객체를 생성하고 그 참조값을 `memberServiceImpl` 을 생성하면서 생성자로 전달한다.
- 클라이언트인 `memberServiceImpl` 입장에서 보면 의존관계를 마치 외부에서 주입해주는 것 같다고 해서 DI(Dependency Injection) 우리 말로 의존관계 주입 또는 의존성 주입이라 한다.

</br></br>
### OrderServiceImpl - 생성자 주입

```groovy
package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{

    //private final MemberRepository memberRepository = new MemoryMemberRepository();
    //private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    //private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        //주문결과 반환
        return new Order(memberId, itemName, itemPrice, discountPrice);

    }
}
```

- 설계 변경으로 `OrderServiceImpl` 은 `FixDiscountPolicy` 를 의존하지 않는다!
- 단지 `DiscountPolicy` 인터페이스만 의존한다.
- `OrderServiceImpl` 입장에서 생성자를 통해 어떤 구현 객체가 들어올지(주입될지)는 알 수 없다.
- `OrderServiceImpl` 의 생성자를 통해서 어떤 구현 객체을 주입할지는 오직 외부( `AppConfig` )에서 결정한다.
- `OrderServiceImpl` 은 이제부터 실행에만 집중하면 된다.
- `OrderServiceImpl` 에는 `MemoryMemberRepository` , `FixDiscountPolicy` 객체의 의존관계가 주입된다.

</br></br>
## AppConfig 실행

### 사용 클래스 - MemberApp

```groovy
package hello.core;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;

public class MemberApp {
public static void main(String[] args) { 
    AppConfig appConfig = new AppConfig();
    MemberService memberService = appConfig.memberService();
    Member member = new Member(1L, "memberA", Grade.VIP);
    memberService.join(member);

    Member findMember = memberService.findMember(1L);
    System.out.println("new member = " + member.getName());
    System.out.println("find Member = " + findMember.getName());
}
}
```

### 사용 클래스 - OrderApp

```groovy
package hello.core;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.order.Order;
import hello.core.order.OrderService;

public class OrderApp {
public static void main(String[] args) {
    AppConfig appConfig = new AppConfig();
    MemberService memberService = appConfig.memberService();
    OrderService orderService = appConfig.orderService();

    long memberId = 1L;
    Member member = new Member(memberId, "memberA", Grade.VIP);
    memberService.join(member);

    Order order = orderService.createOrder(memberId, "itemA", 10000);

    System.out.println("order = " + order);
}
}
```

### 테스트 코드 오류 수정

```groovy
class MemberServiceTest {
MemberService memberService;
@BeforeEach
public void beforeEach() {
AppConfig appConfig = new AppConfig();
memberService = appConfig.memberService();
}
}
```

```groovy
class OrderServiceTest {
MemberService memberService;
OrderService orderService;
@BeforeEach
public void beforeEach() {
AppConfig appConfig = new AppConfig();
memberService = appConfig.memberService();
orderService = appConfig.orderService();
}
}
```
테스트 코드에서 `@BeforeEach` 는 각 테스트를 실행하기 전에 호출된다.

</br></br>
**정리**
- AppConfig를 통해서 관심사를 확실하게 분리했다.
- 배역, 배우를 생각해보자.
- AppConfig는 공연 기획자다.
- AppConfig는 구체 클래스를 선택한다. 배역에 맞는 담당 배우를 선택한다. 애플리케이션이 어떻게 동작해야 할 지 전체 구성을 책임진다.
- 이제 각 배우들은 담당 기능을 실행하는 책임만 지면 된다.
- `OrderServiceImpl` 은 기능을 실행하는 책임만 지면 된다.


# 4. AppConfig 리팩토링
- 현재 AppConfig를 보면 **중복**이 있고, **역할**에 따른 **구현**이 잘 안보인다.

  ***기대하는 그림***
  ![image](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/5fc78649-e25e-447f-975c-1fc0436f1eb9)


## 리펙터링 전

```groovy
package hello.core;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class AppConfig {
public MemberService memberService() {
    return new MemberServiceImpl(new MemoryMemberRepository());
}
public OrderService orderService() {
return new OrderServiceImpl(
    new MemoryMemberRepository(),
    new FixDiscountPolicy());
}
}
```
- 역할이 중복되고, 명확하게 구분되어 있지 않다.
- 중복을 제거하고, 역할에 따른 구현이 보이도록 리팩토링 하자.

</br></br>
## 리팩터링 후

```groovy
package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class AppConfig {

public MemberService memberService() {
    return new MemberServiceImpl(memberRepository());
}

public OrderService orderService() {
    return new OrderServiceImpl(memberRepository(), discountPolicy());
}

public MemberRepository memberRepository() {
    return new MemoryMemberRepository();
}

public DiscountPolicy discountPolicy() {
    return new FixDiscountPolicy();
}

}
```

- `new MemoryMemberRepository()` 부분이 중복 제거되었다. 이제 `MemoryMemberRepository` 를 다른 구현체로 변경할 때 한 부분만 변경하면 된다.
- `AppConfig` 를 보면 역할과 구현 클래스가 한눈에 들어온다. 애플리케이션 전체 구성이 어떻게 되어있는지 빠르게 파악할 수 있다

***참고*** 리팩토링이란?
[리팩토링이란?](https://ikkison.tistory.com/82)


</br></br>
# 5. 새로운 구조와 할인 정책 적용
- 정액 할인 정책을 정률(%) 할인 정책으로 변경해보자
- FixDiscountPolicy -> RateDiscountPolicy
- 어느 부분을 변경하면 되겠는가?

**AppConfig의 등장으로 애플리케이션이 크게 사용 영역과, 객체를 생성하고 구성(Configuration)하는 영역으로 분리되었다.**

**사용 구성의 분리**
![image](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/f404fe82-6bdd-4941-a511-1345ac3ff0a5)

**할인 정책의 변경**
![image](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/3381de4f-f104-4dd6-88c9-8c582b730f58)
- FixDiscountPolicy` `RateDiscountPolicy` 로 변경해도 구성 영역만 영향을 받고, 사용 영역은 전혀 영향을 받지 않는다.


## 할인 정책 변경 구성 코드
```groovy
package hello.core;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
public class AppConfig {

public MemberService memberService() {
    return new MemberServiceImpl(memberRepository());
}

public OrderService orderService() {
    return new OrderServiceImpl(memberRepository(),discountPolicy());
}

public MemberRepository memberRepository() {
    return new MemoryMemberRepository();
}

public DiscountPolicy discountPolicy() {
    // return new FixDiscountPolicy();
    return new RateDiscountPolicy();
}

}
```
- `AppConfig` 에서 할인 정책 역할을 담당하는 구현을 `FixDiscountPolicy` `RateDiscountPolicy` 객체로 변경했다.
- 이제 할인 정책을 변경해도, 애플리케이션의 구성 역할을 담당하는 AppConfig만 변경하면 된다.
- 클라이언트 코드인 `OrderServiceImpl` 를 포함해서 **사용 영역**의 어떤 코드도 변경할 필요가 없다.
- **구성 영역**은 당연히 변경된다.
- 구성 역할을 담당하는 AppConfig를 애플리케이션이라는 공연의 기획자로 생각하자. 공연 기획자는 공연 참여자인 구현 객체들을 모두 알아야 한다.

</br></br>
# 6. 전체 흐름 정리(Chapter 2)

**새로운 할인 정책 개발**
- 다형성 덕분에 새로운 정률 할인 정책 코드를 추가로 개발하는 것 자체는 아무 문제가 없음

**새로운 할인 정책 적용과 문제점**
- 새로 개발한 정률 할인 정책을 적용하려고 하니 **클라이언트 코드**인 주문 서비스 구현체도 함께 변경해야함
- 주문 서비스 클라이언트가 인터페이스인 `DiscountPolicy` 뿐만 아니라, 구체 클래스인 `FixDiscountPolicy` 도 함께 의존 **DIP 위반**

**관심사의 분리**
- 애플리케이션을 하나의 공연으로 생각
- 기존에는 클라이언트가 의존하는 서버 구현 객체를 직접 생성하고, 실행함
- 비유를 하면 기존에는 남자 주인공 배우가 공연도 하고, 동시에 여자 주인공도 직접 초빙하는 다양한 책임을 가지고 있음
- 공연을 구성하고, 담당 배우를 섭외하고, 지정하는 책임을 담당하는 별도의 **공연 기획자**가 나올 시점 공연 기획자인 AppConfig가 등장
- AppConfig는 애플리케이션의 전체 동작 방식을 구성(config)하기 위해, **구현 객체를 생성**하고, **연결**하는 책임
- 이제부터 클라이언트 객체는 자신의 역할을 실행하는 것만 집중, 권한이 줄어듬(책임이 명확해짐)

**AppConfig 리팩터링**
- 구성 정보에서 역할과 구현을 명확하게 분리
- 역할이 잘 드러남
- 중복 제거

**새로운 구조와 할인 정책 적용**
- 정액 할인 정책 정률% 할인 정책으로 변경
- AppConfig의 등장으로 애플리케이션이 크게 **사용 영역**과, 객체를 생성하고 **구성(Configuration)하는 영역**으로 분리
- 할인 정책을 변경해도 AppConfig가 있는 구성 영역만 변경하면 됨, 사용 영역은 변경할 필요가 없음.
- 물론 클라이언트 코드인 주문 서비스 코드도 변경하지 않음


# 7. 좋은 객체 지향 설계의 5가지 원칙의 적용
- SRP, DIP, OCP 적용
  
## SRP 단일 책임 원칙
**한 클래스는 하나의 책임만 가져야 한다.**
- 클라이언트 객체는 직접 구현 객체를 생성하고, 연결하고, 실행하는 다양한 책임을 가지고 있음
- SRP 단일 책임 원칙을 따르면서 관심사를 분리함
- 구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당
- 클라이언트 객체는 실행하는 책임만 담당

## DIP 의존관계 역전 원칙
**프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.” 의존성 주입은 이 원칙을 따르는 방법 중 하나다.**
- 새로운 할인 정책을 개발하고, 적용하려고 하니 클라이언트 코드도 함께 변경해야 했다.
- 왜냐하면 기존 클라이언트 코드( `OrderServiceImpl` )는 DIP를 지키며 `DiscountPolicy` 추상화 인터페이스에 의존하는 것 같았지만, `FixDiscountPolicy` 구체화 구현 클래스에도 함께 의존했다.
- 클라이언트 코드가 `DiscountPolicy` 추상화 인터페이스에만 의존하도록 코드를 변경했다.
- 하지만 클라이언트 코드는 인터페이스만으로는 아무것도 실행할 수 없다.
- AppConfig가 `FixDiscountPolicy` 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드에 의존관계를 주입했다. 이렇게해서 DIP 원칙을 따르면서 문제도 해결했다.

## OCP
**소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다**
- 다형성 사용하고 클라이언트가 DIP를 지킴
- 애플리케이션을 사용 영역과 구성 영역으로 나눔
- AppConfig가 의존관계를 `FixDiscountPolicy` `RateDiscountPolicy` 로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드는 변경하지 않아도 됨
**소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀 있다!**


# 8. IoC, DI, 컨테이너 **중요**

## 제어의 역전 IoC
(Inversion of Control)
- 기존 프로그램은 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성하고, 연결하고, 실행했다. 한마디로 구현 객체가 프로그램의 제어 흐름을 스스로 조종했다. 개발자 입장에서는 자연스러운 흐름이다.
- 반면에 AppConfig가 등장한 이후에 구현 객체는 자신의 로직을 실행하는 역할만 담당한다. 프로그램의 제어 흐름은 이제 AppConfig가 가져간다. 예를 들어서 `OrderServiceImpl` 은 필요한 인터페이스들을 호출하지만 어떤 구현 객체들이 실행될지 모른다.
- 프로그램에 대한 제어 흐름에 대한 권한은 모두 AppConfig가 가지고 있다. 심지어 `OrderServiceImpl` 도 AppConfig가 생성한다. 그리고 AppConfig는 `OrderServiceImpl` 이 아닌 OrderService 인터페이스의 다른 구현 객체를 생성하고 실행할 수 도 있다. 그런 사실도 모른체 `OrderServiceImpl` 은 묵묵히 자신의 로직을 실행할 뿐이다.
- 이렇듯 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전(IoC)이라 한다.

**프레임워크 vs 라이브러리**
- 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다. (JUnit)
- 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다.

</br></br>
## 의존관계 주입 DI
(Dependency Injection)
- OrderServiceImpl` 은 `DiscountPolicy` 인터페이스에 의존한다. 실제 어떤 구현 객체가 사용될지는 모른다.
- 의존관계는 **정적인 클래스 의존 관계와, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계** 둘을 분리해서 생각해야 한다.

**정적인 클래스 의존관계**
- 클래스가 사용하는 import 코드만 보고 의존관계를 쉽게 판단할 수 있다. 정적인 의존관계는 애플리케이션을 실행하지 않아도 분석할 수 있다. 클래스 다이어그램을 보자
- `OrderServiceImpl` 은 `MemberRepository` , `DiscountPolicy` 에 의존한다는 것을 알 수 있다.(implement 관계를 보면 알 수 있다.)
- 그런데 이러한 클래스 의존관계 만으로는 실제 어떤 객체가 `OrderServiceImpl` 에 주입될지 알 수 없다.
### 클래스 다이어그램
![클래스 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/a3e7caa7-cef4-4717-ae41-658fe4476c74)


### 객체 다이어그램
![객체 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/75aa8801-704e-4ed1-8736-66a0d659a632)
- 애플리케이션 **실행 시점(런타임)**에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것을 **의존관계 주입**이라 한다!!!
- 객체 인스턴스를 생성하고, 그 참조값을 전달해서 연결된다.
- 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다.
- 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다.


### IoC 컨테이너, DI 컨테이너
- AppConfig 처럼 객체를 생성하고 관리하면서 의존관계를 연결해주는 것을 IoC 컨테이너 또는 **DI 컨테이너**라 한다.
- 의존관계 주입에 초점을 맞추어 최근에는 주로 DI 컨테이너라 한다.
- 또는 어샘블러, 오브젝트 팩토리 등으로 불리기도 한다.
(DI 컨테이너는 조각들을 레고처럼 조립해주는 역할로 생각하면 된다.)


# 9. 스프링으로 전환하기
- 지금까지 순수한 자바 코드만으로 DI(AppConfig)를 적용했다. 이제 스프링을 사용해보자.

## AppConfig 스프링 기반으로 변경

```groovy
package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

@Bean
public MemberService memberService() {
    return new MemberServiceImpl(memberRepository());
}

@Bean
public OrderService orderService() {
    return new OrderServiceImpl(memberRepository(),discountPolicy());
}

@Bean
public MemberRepository memberRepository() {
    return new MemoryMemberRepository();
}

@Bean
public DiscountPolicy discountPolicy() {
    return new RateDiscountPolicy();
}
}
```
- AppConfig에 설정을 구성한다는 뜻의 `@Configuration` 을 붙여준다.
- 각 메서드에 `@Bean` 을 붙여준다. 이렇게 하면 스프링 컨테이너에 스프링 빈으로 등록한다.


## MemberApp에 스프링 컨테이너 적용

```groovy
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
public static void main(String[] args) {

    // AppConfig appConfig = new AppConfig();
    // MemberService memberService = appConfig.memberService();

    ApplicationContext applicationContext = newAnnotationConfigApplicationContext(AppConfig.class);
    MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

    Member member = new Member(1L, "memberA", Grade.VIP);
    memberService.join(member);

    Member findMember = memberService.findMember(1L);
    System.out.println("new member = " + member.getName());
    System.out.println("find Member = " + findMember.getName());
    }
}
```


## OrderApp에 스프링 컨테이너 적용

```groovy
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.order.Order;
import hello.core.order.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
public static void main(String[] args) {

    // AppConfig appConfig = new AppConfig();
    // MemberService memberService = appConfig.memberService();
    // OrderService orderService = appConfig.orderService();

    ApplicationContext applicationContext = new
    AnnotationConfigApplicationContext(AppConfig.class);
    MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
    OrderService orderService = applicationContext.getBean("orderService",OrderService.class);

    long memberId = 1L;
    Member member = new Member(memberId, "memberA", Grade.VIP);
    memberService.join(member);

    Order order = orderService.createOrder(memberId, "itemA", 10000);
    System.out.println("order = " + order);
}
}
```

### 스프링 컨테이너
- ApplicationContext` 를 스프링 컨테이너라 한다.
- 기존에는 개발자가 `AppConfig` 를 사용해서 직접 객체를 생성하고 DI를 했지만, 이제부터는 스프링 컨테이너를 통해서 사용한다.
- 스프링 컨테이너는 `@Configuration` 이 붙은 `AppConfig` 를 설정(구성) 정보로 사용한다. 여기서 `@Bean` 이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다.
- 스프링 빈은 `@Bean` 이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다. ( `memberService` ,`orderService` )
- 이전에는 개발자가 필요한 객체를 `AppConfig` 를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통해서 필요한 스프링 빈(객체)를 찾아야 한다. 스프링 빈은 `applicationContext.getBean()` 메서드를 사용해서 찾을 수 있다.
- 기존에는 개발자가 직접 자바코드로 모든 것을 했다면 이제부터는 스프링 컨테이너에 객체를 스프링 빈으로 등록하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.
- 코드가 약간 더 복잡해진 것 같은데, 스프링 컨테이너를 사용하면 어떤 장점이 있을까?


### 스프링 부트 3.1 이상 - 로그 출력 안되는 문제 해결
`MemberApp` 과 `OrderApp` 을 실행할 때, 스프링 부트 3.1 이상을 사용한다면 로그가 출력되지 않는다.

스프링 부트 3.1 미만**
```
19:18:00.439 [main] DEBUG
org.springframework.context.annotation.AnnotationConfigApplicationContext -
Refreshing
org.springframework.context.annotation.AnnotationConfigApplicationContext@7cdbc5
d3
19:18:00.445 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean
'org.springframework.context.annotation.internalConfigurationAnnotationProcessor
'
19:18:00.503 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean
'org.springframework.context.event.internalEventListenerProcessor'
19:18:00.504 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean
'org.springframework.context.event.internalEventListenerFactory'
19:18:00.504 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean
'org.springframework.context.annotation.internalAutowiredAnnotationProcessor'
19:18:00.505 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean
'org.springframework.context.annotation.internalCommonAnnotationProcessor'
19:18:00.508 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean 'appConfig'
19:18:00.510 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean 'memberService'
19:18:00.512 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean 'memberRepository'
19:18:00.512 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean 'orderService'
19:18:00.513 [main] DEBUG
org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating
shared instance of singleton bean 'discountPolicy'
new member = memberA
find Member = memberA
```
**스프링 부트 3.1 이상**
```
new member = memberA
find Member = memberA
```
이때는 다음 위치에 파일을 만들어서 넣으면 된다.


src/main/resources/logback.xml`
```xml
<configuration>
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
<encoder>
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%kvp-
%msg%n</pattern>
</encoder>
</appender>
<root level="DEBUG">
<appender-ref ref="STDOUT" />
</root>
</configuration>
```
- 스프링 부트 3.1 부터 기본 로그 레벨을 `INFO` 로 빠르게 설정하기 때문에 로그를 확인할 수 없는데, 이렇게하면 기본 로그 레벨을 `DEBUG` 로 설정해서 강의 내용과 같이 로그를 확인할 수 있다.
- 참고로 이 내용은 `MemberApp` 과 `OrderApp` 처럼 `ApplicationContext` 를 직접 생성해서 사용할 때만 적용된다.
- 강의 뒤에서 나오는 `CoreApplication` 처럼 스프링 부트를 실행할 때는 이 파일을 제거하거나 또는 `<rootlevel="DEBUG">` 부분을 `<root level="INFO">` 로 변경하면 강의 내용과 같은 로그를 확인할 수 있다.

</br></br>
# Chapter 3 스프링 컨테이너와 스프링 빈

# 1. 스프링 컨테이너 생성

## 스프링 컨테이너가 생성되는 과정을 알아보자

```groovy
//스프링 컨테이너 생성
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```
- `ApplicationContext` 를 스프링 컨테이너라 한다.
- `ApplicationContext` 는 인터페이스이다.
- 스프링 컨테이너는 XML을 기반으로 만들 수 있고, 애노테이션 기반의 자바 설정 클래스로 만들 수 있다.
- 직전에 `AppConfig` 를 사용했던 방식이 애노테이션 기반의 자바 설정 클래스로 스프링 컨테이너를 만든 것이다.
- 자바 설정 클래스를 기반으로 스프링 컨테이너( `ApplicationContext` )를 만들어보자.
    - `new AnnotationConfigApplicationContext(AppConfig.class);`
    - 이 클래스는 `ApplicationContext` 인터페이스의 구현체이다.

***참고***: 더 정확히는 스프링 컨테이너를 부를 때 `BeanFactory` , `ApplicationContext` 로 구분해서 이야기한다. 
`BeanFactory` 를 직접 사용하는 경우는 거의 없으므로 일반적으로 `ApplicationContext` 를 스프링 컨테이너라 한다.

</br></br>
## 스프링 컨테이너의 생성 과정

### 1. 스프링 컨테이너 생성

![스프링 컨테이너](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/49128899-5db7-4cfb-a2f7-04822b54d1ef)
- `new AnnotationConfigApplicationContext(AppConfig.class)`
- 스프링 컨테이너를 생성할 때는 구성 정보를 지정해주어야 한다.
- 여기서는 `AppConfig.class` 를 구성 정보로 지정했다.

### 2. 스프링 빈 등록
![ApConfig](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/b18c0f6c-56d3-4860-bdc5-5d249e01496f)
- 스프링 컨테이너는 파라미터로 넘어온 설정 클래스 정보를 사용해서 스프링 빈을 등록한다.

***빈 이름***
- 빈 이름은 메서드 이름을 사용한다.
- 빈 이름을 직접 부여할 수도 있다.
    - @Bean(name="memberrrrrr")
- **단, 빈 이름은 항상 다른 이름을 부여해야 한다.**
- 같은 이름을 부여하면 다른 빈이 무시되거나, 기존 빈을 덮어버리거나 설정에 따라 오류가 발생된다.

### 3. 스프링 빈 의존관계 설정 - 준비
![준비](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/e6ce4cb1-64f7-4c7d-b3f4-378d88505990)

### 4. 스프링 빈 의존관계 설정 - 완료
![완료](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/d3f68428-ccc3-417d-946e-fa0f39760464)
- 스프링 컨테이너는 설정 정보를 참고해서 의존관계를 주입(DI)한다.
- 단순히 자바 코드를 호출하는 것 같지만, 차이가 있다. 이 차이는 뒤에 싱글톤 컨테이너에서 설명한다.

</br></br>
**참고**
스프링은 빈을 생성하고, 의존관계를 주입하는 단계가 나누어져 있다. 그런데 이렇게 자바 코드로 스프링 빈을 등록하면 생성자를 호출하면서 의존관계 주입도 한번에 처리된다. 여기서는 이해를 돕기 위해 개념적으로 나누어 설명했다.

**정리**
스프링 컨테이너를 생성하고, 설정(구성) 정보를 참고해서 스프링 빈도 등록하고, 의존관계도 설정했다. 이제 스프링 컨테이너에서 데이터를 조회해보자.


</br></br>
# 2. 컨테이너에 등록된 모든 빈 조회

```groovy
package hello.core.beanfind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
class ApplicationContextInfoTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name=" + beanDefinitionName + " object=" +
            bean);
        }
    }

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition =ac.getBeanDefinition(beanDefinitionName);
            //Role ROLE_APPLICATION: 직접 등록한 애플리케이션 빈
            //Role ROLE_INFRASTRUCTURE: 스프링이 내부에서 사용하는 빈
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name=" + beanDefinitionName + " object=" + bean);
            }
        }
    }
}
```

**모든 빈 출력하기**
- 실행하면 스프링에 등록된 모든 빈 정보를 출력할 수 있다.
- `ac.getBeanDefinitionNames()` : 스프링에 등록된 모든 빈 이름을 조회한다.
- `ac.getBean()` : 빈 이름으로 빈 객체(인스턴스)를 조회한다.


**애플리케이션 빈 출력하기**
- 스프링이 내부에서 사용하는 빈은 제외하고, 내가 등록한 빈만 출력해보자.
- 스프링이 내부에서 사용하는 빈은 `getRole()` 로 구분할 수 있다.
    - `ROLE_APPLICATION` : 일반적으로 사용자가 정의한 빈
    - `ROLE_INFRASTRUCTURE` : 스프링이 내부에서 사용하는 빈

</br></br>
# 3. 스프링 빈 조회 - 기본

**스프링 컨테이너에서 스프링 빈을 찾는 가장 기본적인 조회 방법**
- `ac.getBean(빈이름, 타입)`
- `ac.getBean(타입)`
- 조회 대상 스프링 빈이 없으면 예외 발생
    - `NoSuchBeanDefinitionException: No bean named 'xxxxx' available`

```groovy
package hello.core.beanfind;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.assertj.core.api.Assertions.*;

class ApplicationContextBasicFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

@Test
@DisplayName("빈 이름으로 조회")
void findBeanByName() {
    MemberService memberService = ac.getBean("memberService",MemberService.class);
    assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
}

@Test
@DisplayName("이름 없이 타입만으로 조회")
void findBeanByType() {
    MemberService memberService = ac.getBean(MemberService.class);
    assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
}

@Test
@DisplayName("구체 타입으로 조회")
void findBeanByName2() {
    MemberServiceImpl memberService = ac.getBean("memberService",
    MemberServiceImpl.class);
    assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
}

@Test
@DisplayName("빈 이름으로 조회X")
void findBeanByNameX() {
    //ac.getBean("xxxxx", MemberService.class);
    Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("xxxxx", MemberService.class));
}
}
```
참고: 구체 타입으로 조회하면 변경시 유연성이 떨어진다.

</br></br>
# 4. 스프링 빈 조회 - 동일한 타입이 둘 이상
- 타입으로 조회시 같은 타입의 스프링 빈이 둘 이상이면 오류가 발생한다. 이때는 빈 이름을 지정하자.
- `ac.getBeansOfType()` 을 사용하면 해당 타입의 모든 빈을 조회할 수 있다.

```groovy
package hello.core.beanfind;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextSameBeanFindTest {
    AnnotationConfigApplicationContext ac = newAnnotationConfigApplicationContext(SameBeanConfig.class);
@Test
@DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 중복 오류가 발생한다")
void findBeanByTypeDuplicate() {
    //MemberRepository bean = ac.getBean(MemberRepository.class);
    assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(MemberRepository.class));
}

@Test
@DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 빈 이름을 지정하면 된다")
void findBeanByName() {
    MemberRepository memberRepository = ac.getBean("memberRepository1",MemberRepository.class);
    assertThat(memberRepository).isInstanceOf(MemberRepository.class);
}

@Test
@DisplayName("특정 타입을 모두 조회하기")
void findAllBeanByType() {
    Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);
    for (String key : beansOfType.keySet()) {
    System.out.println("key = " + key + " value = " + beansOfType.get(key));
}
    System.out.println("beansOfType = " + beansOfType);
    assertThat(beansOfType.size()).isEqualTo(2);
}

@Configuration
static class SameBeanConfig {
    @Bean
    public MemberRepository memberRepository1() {
    return new MemoryMemberRepository();
}
    @Bean
    public MemberRepository memberRepository2() {
    return new MemoryMemberRepository();
    }
}
}
```

# 5. 스프링 빈 조회 - 상속 관계

- 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
- 그래서 모든 자바 객체의 최고 부모인 `Object` 타입으로 조회하면, 모든 스프링 빈을 조회한다.

![상속관계](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/002ee3a9-b6ff-48be-b2d4-53adfab82919)

</br></br>
```groovy
package hello.core.beanfind;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextExtendsFindTest {
AnnotationConfigApplicationContext ac = newAnnotationConfigApplicationContext(TestConfig.class);

@Test
@DisplayName("부모 타입으로 조회시, 자식이 둘 이상 있으면, 중복 오류가 발생한다")
void findBeanByParentTypeDuplicate() {
    //DiscountPolicy bean = ac.getBean(DiscountPolicy.class);
    assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(DiscountPolicy.class));
}

@Test
@DisplayName("부모 타입으로 조회시, 자식이 둘 이상 있으면, 빈 이름을 지정하면 된다")
void findBeanByParentTypeBeanName() {
    DiscountPolicy rateDiscountPolicy = ac.getBean("rateDiscountPolicy",DiscountPolicy.class);
    assertThat(rateDiscountPolicy).isInstanceOf(RateDiscountPolicy.class);
}

@Test
@DisplayName("특정 하위 타입으로 조회")
void findBeanBySubType() {
    RateDiscountPolicy bean = ac.getBean(RateDiscountPolicy.class);
    assertThat(bean).isInstanceOf(RateDiscountPolicy.class);
}

@Test
@DisplayName("부모 타입으로 모두 조회하기")
void findAllBeanByParentType() {
    Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);
    assertThat(beansOfType.size()).isEqualTo(2);
    for (String key : beansOfType.keySet()) {
    System.out.println("key = " + key + " value=" + beansOfType.get(key));
    }
}

@Test
@DisplayName("부모 타입으로 모두 조회하기 - Object")
void findAllBeanByObjectType() {
    Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);
    for (String key : beansOfType.keySet()) {
    System.out.println("key = " + key + " value=" + beansOfType.get(key));
    }
}

@Configuration
static class TestConfig {

@Bean
public DiscountPolicy rateDiscountPolicy() {
    return new RateDiscountPolicy();
}

@Bean
public DiscountPolicy fixDiscountPolicy() {
    return new FixDiscountPolicy();
    }
}
}
```
</br></br>
# 6. BeanFactory와 ApplicationContext

![BeanFactory & ApplicationContext](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/b21ec272-701d-4648-8724-c1d0259d98c4)

## BeanFactory
- 스프링 컨테이너의 최상위 인터페이스이다.
- 스프링 빈을 관리하고 조회하는 역할을 담당한다.
- getBean()을 제공한다.
- 지금까지 우리가 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능이다.

## ApplicationContext
- BeanFactory 기능을 모두 상속받아서 제공한다.
- 빈을 관리하고 검색하는 기능을 BeanFactory가 제공해주는데, 그러면 둘의 차이가 뭘까?
- 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.

### ApplicatonContext가 제공하는 부가기능

![image](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/ec29c810-aea2-47a0-9b8f-25f8c80674dc)

- **메시지소스를 활용한 국제화 기능**
    - 예를 들어서 한국에서 들어오면 한국어로, 영어권에서 들어오면 영어로 출력
- **환경변수**
    - 로컬, 개발, 운영등을 구분해서 처리
- **애플리케이션 이벤트**
    - 이벤트를 발행하고 구독하는 모델을 편리하게 지원
- **편리한 리소스 조회**
    - 파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회
</br></br>

**정리**
- ApplicationContext는 BeanFactory의 기능을 상속받는다.
- ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공한다.
- BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용한다.
- BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다.

# 7. 다양한 설정 형식 지원 - 자바 코드, XML

- 스프링 컨테이너는 다양한 형식의 설정 정보를 받아드릴 수 있게 유연하게 설계되어 있다.
    - 자바 코드, XML, Groovy 등
 
  
 ![image](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/5d94d29a-d190-408d-831e-a65df681c78c)

## 애노테이션 기반 자바 코드 설정 사용
- 지금까지 했던 것이다.
- `new AnnotationConfigApplicationContext(AppConfig.class)`
- `AnnotationConfigApplicationContext` 클래스를 사용하면서 자바 코드로된 설정 정보를 넘기면 된다

## XML 설정 사용
- 최근에는 스프링 부트를 많이 사용하면서 XML기반의 설정은 잘 사용하지 않는다.
- 아직 많은 레거시 프로젝트들이 XML로 되어 있고, 또 XML을 사용하면 컴파일 없이 빈 설정 정보를 변경할 수 있는 장점도 있으므로 한번 배워두는 것도 괜찮다.
- `GenericXmlApplicationContext` 를 사용하면서 `xml` 설정 파일을 넘기면 된다.

### XmlAppConfig 사용 자바 코드
```xml
package hello.core.xml;

import hello.core.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import static org.assertj.core.api.Assertions.*;

public class XmlAppContext {

@Test
void xmlAppContext() {
ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
MemberService memberService = ac.getBean("memberService", MemberService.class);
assertThat(memberService).isInstanceOf(MemberService.class);
    }
}
```

### xml 기반의 스프링 빈 설정 정보
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://
www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="memberService" class="hello.core.member.MemberServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
    </bean>

    <bean id="memberRepository"
        class="hello.core.member.MemoryMemberRepository" />

    <bean id="orderService" class="hello.core.order.OrderServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
        <constructor-arg name="discountPolicy" ref="discountPolicy" />
    </bean>

    <bean id="discountPolicy" class="hello.core.discount.RateDiscountPolicy" />
</beans>
```

- xml 기반의 `appConfig.xml` 스프링 설정 정보와 자바 코드로 된 `AppConfig.java` 설정 정보를 비교해보면 거의 비슷하다는 것을 알 수 있다.
- xml 기반으로 설정하는 것은 최근에 잘 사용하지 않으므로 이정도로 마무리 하고, 필요하면 스프링 공식 레퍼런스 문서를 확인하자.
    - https://spring.io/projects/spring-framework
