package com.shop.service;

import com.shop.dto.ItemImgDto;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemImgService {
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {

        //multipart.class -> getOriginalFilename
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //스프링에서 제공, isEmpty - 문자열이 nulll인지 확인, null일 경우 false 반환
        if (!StringUtils.isEmpty(oriImgName)) {
            //itemImgLocation=C:/shop/item applilcation.properties에 저장되어있음
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);

    }

    public List<ItemImgDto> getItemImgDtoList(Long itemId) {
        List<ItemImg> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        return itemImgDtoList;
    }


    //itemForm 에 name = itemImgFile
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
        throws Exception{
        if(!itemImgFile.isEmpty()) {
            //crudRepository(findById)
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
                //fileService의 deleteFile 메소드
                //비어있지 않으면(이미지가 존재하던 상태면) 원래있던 이미지를 삭제하고 새로운 이미지로 대체(업데이트)
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl);
            //itemForm.html 파일에서 받아옴
        }


    }
}
