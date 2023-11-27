package hello.core.singleton;
public class StatefulService {
    private int price; //상태를 유지하는 필드 10000 -> 20000원으로 초기화된다.
    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price; //여기가 문제!
    }

    /*해결 방법:
    private int price; => 전역변수를 삭제해주고 지역변수로 바꿔줘야 한다.
    바로 가격을 찍어준다.

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        return price
    }
    */




    public int getPrice() {
        return price;
    }
}