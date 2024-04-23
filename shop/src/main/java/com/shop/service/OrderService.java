package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;


    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        //상품 조회
        Member member = memberRepository.findByEmail(email);
        //이메일을 통해 회원 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();
        //빈 리스트 생성
        OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
        //orderItem 객체 생성
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        //repository를 통해 데이터베이스에 저장
        return order.getId();
    }

    @Transactional(readOnly=true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email,pageable);
        //이메일, 페이징 사용해서 주문 목록 조회
        Long totalCount = orderRepository.countOrder(email);
        //총 주문 개수

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            //해당 주문에 대한 정보 가져옴
            List<OrderItem> orderItems = order.getOrderItems();
            //orderItem 조회(주문 상품목록)
            for(OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                //주문상품의 대표이미지 조회
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                //orderItem 과 찾은 대표 이미지를 이용해 orderItemDto 객체를 생성 후, orderHistDto 객체에 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
            //각 주문마다 반복하기 위해 2중 for문 사용
        }
        return new PageImpl<OrderHistDto>(orderHistDtos,pageable,totalCount);
        //스프링에서 제공, orderHistDto가 페이지에 포함될 항목,
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();
        //email 로 찾은 현재 memeber(curMember)와 orderId가 같은지 다른지 검사

        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            //일치하지 않으면 false
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    //장바구니에서 주문할 상품 데이터를 전달 받아 주문생성하는 로직
    public Long orders(List<OrderDto> orderDtoList,String email){
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for(OrderDto orderDto : orderDtoList) {
            //주문할 상품 리스트 생성
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
            orderItemList.add(orderItem);
        }
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}
