package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    //itemformDto에 데이터 저장
    //view resolver에 의해 return -
    @GetMapping("admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "item/itemForm";
    }

    //itemForm 의 input type= file name=itemImgFile에서 정보들을 받아와 itemImgFileList에 저장
    @PostMapping("admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model
            , @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        System.out.println("1234");
        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }
            //itemFormDto.getId()==null -> 상품의 id가 비어있음(새 상품임)
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId()==null) {
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch(Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생하였습니다");
        }
        return "redirect:/";
    }

    @GetMapping("admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto",itemFormDto);
        }catch(EntityNotFoundException e) {
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }


    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {

        if(bindingResult.hasErrors()) {
            return "item/itemForm" ;
        }
            if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId()==null){
                model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다. ");
                return "item/itemForm" ;
            }
            try{
                itemService.updateItem(itemFormDto, itemImgFileList);
            }catch (Exception e) {
                model.addAttribute("errorMessage","상품 수정 중 에러가 발생했습니다.");
                return "item/itemForm" ;
            }


        return "redirect:/";
    }

    @GetMapping(value = {"admin/items","admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page") Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get(): 0,3);
        //1 페이지당 3개의 항목만(size) , 페이지 번호가 없을시 0페이지를 조회 (page is present 삼항 연산자)

        //스프링에서 지원되는 페이지 정렬 내역
        Page<Item> items =
                itemService.getAdminItemPage(itemSearchDto, pageable);
            model.addAttribute("items",items);
            model.addAttribute("itemSearchDto",itemSearchDto);
            model.addAttribute("maxPage",5);
            //itemMng.html 에서 타임리프로 호출될 값들
            return "item/itemMng";

    }

    @GetMapping("item/{itemId}")
    //@PathVariable - 요청 url의 일부를 매개변수에 바인딩할때 사용
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item",itemFormDto);
        return "item/itemDtl";
    }


}
