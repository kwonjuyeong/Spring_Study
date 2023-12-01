package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
//@Qualifier("mainDiscountPolicy") - Qualifier로 이름 지정해주고 사용할 수 있고, @Primary로 우선 선택이 되게 할 수 있다.
@Primary
public class RateDiscountPolicy implements DiscountPolicy{
    //할인율 10%
    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return price* discountPercent / 100;
        }else {
            return 0;
        }
    }
}
