package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("select o from Order o " + " where o.member.email =:email " +  "order by o.orderDate desc"

            //JPQL 사용중, 조회 조건이 복잡하지 않은 경우 QueryDsl이 아닌 @Query어노테이션 사용 가능
            //그래도 컴파일 시점에 확인 가능하다는 큰 장점이 queryDsl에게 있어서 보통 QueryDsl사용

    )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    @Query("select count(o) from Order o " +
            "where o.member.email=:email"
    )
    Long countOrder(@Param("email") String email);
}

