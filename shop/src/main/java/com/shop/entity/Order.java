package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter

public class Order extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //orderItem의 order Order - 부모, OrderItem - 자식
    //CascadeType.ALL 부모 엔티티의 영속 상태 변화가 자식에게 영향을 미침 -> 주문 삭제시 OrderItem에서도 삭제됨
    //orphanRemoval = true -> 고아 객체 제거 기능 (참조하는 곳이 하나일때만 사용가능)
    //fetch = FetchType.LAZY 지연 로딩 방식 - 즉시로딩 사용시 사용하지 않는 데이터를 가져오기 때문에 실무에서 사용힘듬
    @OneToMany(mappedBy= "order",cascade = CascadeType.ALL ,
                orphanRemoval = true, fetch = FetchType.LAZY)

    private List<OrderItem> orderItems = new ArrayList<>();


    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        //회원 정보 세팅
        order.setMember(member);
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        //장바구니에는 여러가지 상품이 한번에 들어가고 주문될수있기때문에 리스트 형태로 값을 받음
        order.setOrderStatus(OrderStatus.ORDER);
        //주문 상태를 order로 세팅
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }


}
