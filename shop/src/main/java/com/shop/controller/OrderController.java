package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    //스프링에서 비동기처리를할때 사용하는 어노테이션 , requestBody : http요청 본문 body에 담긴 내용을 객체로 전달, responseBody : 자바 객체를 body로 전달,
    // xml이나 json기반 메세지 전달시  유용
    //itemDtl 에서 ajax로 비동기 통신한 데이터 값을 받음 (매개변수의 orderDto 객체가 받은상태)

    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                            BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            StringBuilder sb =new StringBuilder();
            List<FieldError> filedErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError:filedErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            //오류 메세지 전부 string builder에 합친 뒤 클라이언트에 반환
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
            //responseEntity로 http응답 생성, 제어(BAD_REQUEST)
        }
        String email = principal.getName();
        //현재 인증된 사용자를 나타내는 principal 객체. getName 사용시 현재 로그인한 사용자명을 알 수 있다 .현재는 이메일 정보 조회
        Long orderId;
        try{
            orderId = orderService.order(orderDto, email);
        }catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

    @GetMapping(value = {"orders","orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get(): 0,4);

        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders",ordersHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);
        return "order/orderHist";

    }

    @PostMapping("order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder
            (@PathVariable("orderId") Long orderId, Principal principal){
        if (!orderService.validateOrder(orderId, principal.getName())){
            //현재 email로 찾은 curMemeber와 userId가 일치 하지 않으면 주문 취소 권한이 없다고 출력
            return new ResponseEntity<String>("주문 취소 권한이 없습니다",HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
