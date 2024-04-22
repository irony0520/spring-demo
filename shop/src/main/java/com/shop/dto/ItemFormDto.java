package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {
    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    @Digits(integer = 8,fraction = 3,message = "유효한 숫자를 입력해주세요.")
    private Integer price;

    @NotBlank(message = "상품 세부설명은 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    @Digits(integer = 8,fraction = 2,message = "유효한 숫자를 입력해주세요.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();
    private static ModelMapper modelMapper = new ModelMapper();

    //modelMapper를 사용해 itemFormDto 객체의 데이터를 item 엔티티로 생성
    //즉 item엔티티로 변환된 데이터는 영속성 콘텍스트가 관리, 데이터베이스에 저장된 후에는 영속화됨
    public Item createItem() {
        return modelMapper.map(this, Item.class);
        //itemFormDto 객체-> item 엔티티
    }

    public static ItemFormDto of(Item item) {
        return modelMapper.map(item, ItemFormDto.class);
        //item엔티티 -> itemFormDto 객체
    }
    //왜 이런식으로 코드를 짜는가 ? - > 데이터 전환을 용이하게 만들고 레이어 분리 코드 재사용 용이 ....... . 등등
    public List<Long> itemImgIds = new ArrayList<>();

}
