package com.shop.controller;


import com.shop.dto.ItemDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/thymeleaf")
public class ThymeleafExController {

    @GetMapping("/ex01")
    public String thymeleafExample01(Model model){
        model.addAttribute("data","타임리프 예제");
        return "thymeleafEx/thymeleafEx01";
    }

    @GetMapping("/ex02")
    public String thymeleafExample02(Model model) {
        ItemDTO itemDto = new ItemDTO();
        itemDto.setItemDetail("상품 상세 설명");
        itemDto.setItemNm("테스트상품");
        itemDto.setPrice(10000);
        itemDto.setRegTime(LocalDateTime.now());

        model.addAttribute("itemDto",itemDto);
        return "thymeleafEx/thymeleafEx02";

    }

    @GetMapping("/ex03")
    public String thymeleafExample03(Model model){
        List<ItemDTO> itemDTOList = new ArrayList<>();

        for(int i =0;i<=10;i++) {
            ItemDTO itemDto = new ItemDTO();
            itemDto.setItemDetail("상품 상세 설명"+i);
            itemDto.setItemNm("테스트상품"+i);
            itemDto.setPrice(10000+i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDTOList.add(itemDto);
        }
        model.addAttribute("itemDTOList",itemDTOList);
        return "thymeleafEx/thymeleafEx03";

    }

    @GetMapping("/ex04")
    public String thymeleafExample04(Model model){
        List<ItemDTO> itemDTOList = new ArrayList<>();

        for(int i =1;i<=10;i++) {
            ItemDTO itemDto = new ItemDTO();
            itemDto.setItemDetail("상품 상세 설명"+i);
            itemDto.setItemNm("테스트상품"+i);
            itemDto.setPrice(1000*i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDTOList.add(itemDto);
        }
        model.addAttribute("itemDTOList",itemDTOList);
        return "thymeleafEx/thymeleafEx04";

    }

    @GetMapping("/ex05")
    public String thymeleafExample05() {
        return "thymeleafEx/thymeleafEx05";
    }

    @GetMapping("/ex06")
    public String thymeleafExample06(String param1,String param2, Model model) {

        model.addAttribute("param1",param1);
        model.addAttribute("param2",param2);
        return "thymeleafEx/thymeleafEx06";
    }

    @GetMapping("/ex07")
    public String thymeleafExample07() {
        return "thymeleafEx/thymeleafEx07";
    }
}
