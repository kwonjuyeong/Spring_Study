package hello.core.member;

public interface MemberRepository {

    //멤버 정보 저장
    void save(Member member);

    //멤버 찾기
    Member findById(Long memberId);

}
