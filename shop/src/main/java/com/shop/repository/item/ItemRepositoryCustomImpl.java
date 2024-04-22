package com.shop.repository.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {


    private final QItem item = QItem.item;
    //Qitem은 item의 QueryDSl용 클래스
    private final JPAQueryFactory queryFactory;
    //동적 쿼리 생성 jpa = 자바 ORM 기술 표준으로 POJO객체와 DB 데이터 간의 매핑을 처리하기 위한 ORM 표준을 의미한다
    //JPAQueryFactory란 JPA의 엔터티를 이용하여 JPQLQuery를 보다 쉽고 편리하게 작성할 수 있는 QueryDsl의 도구라 할 수 있다. , pom.xml에 추가 필요


    //동적쿼리, QueryDsl에서 사용되는 논리 표현식으로 where절의 역할을 함 and, or같은 연산자를 결합해 논리 표현식을 만들 수 있게되며, 쿼리 메서드에 전달돼 조건에 맞는 데이터를 찾을 수 있
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus ==
                null ? null : item.itemSellStatus.eq(searchSellStatus);
        //eq = equal, itemSellStatus = 생성된 qitem의 클래스
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        //searchQuery - 검색어 itemNm로 검색할때와 createdBy로 검색할때
        if (StringUtils.equals("itemNm", searchBy)) {
            return item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        //ItemRepositoryCustom을 상속 받아서 여기에 작성됨
        List<Item> content = queryFactory
                .selectFrom(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(Wildcard.count)
                .from(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                //BooleanExpression으로 만들어져서 where절 사용 가능,null반환 시 where절에서 컷내줌  각각 검색일자, 상품의 status , 검색조건+검색어,
                .fetchOne();
                //quety dsl 조회 메소드 , fetchOne 의 경우 조회 대상이 1건이면 해당 타입 반환, 1개 초과될시 에러 발생 .

        if (total == null)
            total = 0L;
        //total -> 전체 항목 수

        return new PageImpl<>(content, pageable, total);
        //페이징 결과 출력
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(searchByLike("itemNm",itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(searchByLike("itemNm",itemSearchDto.getSearchQuery()))
                .fetchOne();

        if(total == null) 
            total = 0L;
        return new PageImpl<>(content, pageable, total);
    }
}

