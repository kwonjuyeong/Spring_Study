package hello.core.order;

//클라이언트 -> 주문생성(아이디, 아이템, 가격) -> 주문 결과 반환
public interface OrderService {
    Order createOrder(Long memberId, String itemName, int itemPrice);


}
