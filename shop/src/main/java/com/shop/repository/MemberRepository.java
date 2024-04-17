package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


//MemberController에 memberFormDto에 저장된 데이터를 불러오는데, memberFormDto의 경우 엔티티가 아닌 단순한 객체이기 때문에
//member를 extend 하게 되고, MemberService에서 의존성이 주입된 후 계속 쓰이게됨
//클라이언트가 memberForm 에서 name,email,password,address등을 입력하면 controller가 그걸 memberFormDto객체에 저장하고,
//그걸 service가 받으며 필요한 정보를 추출하고 entity로 변환한 후 repository를 통해 데이터베이스에 저장함.
public interface MemberRepository extends JpaRepository <Member,Long> {
    Member findByEmail(String email);

    //Member findByEmailAndPassword(String email, String password);

}
