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
