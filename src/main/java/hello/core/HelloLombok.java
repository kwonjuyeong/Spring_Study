package hello.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//롬복 설치 확인용 클래스
@Getter
@Setter
@ToString
public class HelloLombok {

    private String name;
    private int age;

    public static void main(String[] args){
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("hello k");

        //String name = helloLombok.getName();
        System.out.println("helloLombok = " + helloLombok);

    }

}
