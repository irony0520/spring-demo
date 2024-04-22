package com.shop.repository.item;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

   //Pageable -> 자바 스프링에서 제공하는 페이지네이션 기능 사용가능
   Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

   Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}
