package com.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//auditing을 적용하기 위한 어노테이션
@EntityListeners(value = {AuditingEntityListener.class})
//객체의 입장에서 공동 매핑정보가 필요할 때 사용, db와는 관계없음(연동x), 지금은 생성시간, 수정시간들이 반복적으로 공통되고 있음
//현재 baseEntity를 상속받은 entity들은 모두 생성시간, 수정시간의 매핑 정보들도 상속받는중
@MappedSuperclass
@Getter

public abstract class BaseEntity extends BaseTimeEntity{
    //각각 생성시간, 수정시간(변경될 때의 시간)을 저장하는 어노테이션
    @CreatedDate
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedDate
    private String modifiedBy;


}
