package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemImgDto {

    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();

    //itemImg객체를 ItemImgDto로 변환시킨다는걸 더 쉽게 알수 있도록 가독성 증가를 위해 of를 붙여줌
    public static ItemImgDto of(ItemImg itemImg) {
        //객체의 프로퍼티를 다른 객체의 프로퍼티로 매핑해주는 유틸리티
        //setter, getter를 필드마다 일일히 열거해가며 매핑시키지 않을 수 있게 해준다.
        //필드 이름이 일치해야하고(매핑 관계 추가를 통해 극복가능) , 복잡한 타입변환은 안된다고 한다.
        //현재는 itemImgDto로 값을 반환 중
        return modelMapper.map(itemImg,ItemImgDto.class);
    }
}
