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

![회원 객체 인스턴스 다이어그램](https://github.com/kwonjuyeong/Spring_Study/assets/57522230/76462021-e53b-436e-9aaa-d55823b95b7c)

- `appConfig` 객체는 `memoryMemberRepository` 객체를 생성하고 그 참조값을 `memberServiceImpl` 을 생성하면서 생성자로 전달한다.
- 클라이언트인 `memberServiceImpl` 입장에서 보면 의존관계를 마치 외부에서 주입해주는 것 같다고 해서 DI(Dependency Injection) 우리 말로 의존관계 주입 또는 의존성 주입이라 한다.


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
