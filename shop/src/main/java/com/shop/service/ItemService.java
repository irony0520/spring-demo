package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    //itemFromDto의 상품정보와 , 이미지가 담겨있는 itemImgFileList를 가져옴
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemFormDto.createItem();
        //데이터 베이스에 저장
        itemRepository.save(item);

        for(int i=0;i<itemImgFileList.size();i++) {
            //새로운 itemImg 엔티티 생성
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i ==0)
                //대표이미지(Y)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            //itemImgService의 saveItemImg 메소드
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional//(readOnly=true)
    public ItemFormDto getItemDtl(Long itemId) {

        //crudRepository
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        //optional 객체에 값이 없을때 notFoundException 발생
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        //item 객체를 itemFormDto객체로 변환 -> 프레젠테이션 레이어(컨트롤러) 의 데이터를 service에서 사용할수 있게 service layer로 변경
        //controller가 service를 의존
        itemFormDto.setItemImgDtoList(itemImgService.getItemImgDtoList(itemId));
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        //itemFrom 에 있는 itemImgIds 클라이언트가 입력하면 controller 저장 dto를 거쳐서 여기로 호출
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
            //상품 이미지 아이디와 , 상품 이미지 파일 정보 전달
        }
        return item.getId();
    }

    @Transactional(readOnly =true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly =true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }
}



