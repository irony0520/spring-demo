package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter @Setter
@ToString
public class Cart extends BaseEntity{
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @OneToOne
    @JoinColumn(name="member_id")
    private Member member;
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        //회원 엔티티 파라미터를 받아 장바구니 엔티티 생성
        return cart;
    }

}
