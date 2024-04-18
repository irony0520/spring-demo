package com.boot.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="refreshToken")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id",updatable = false)
    private Long id;


    //user 엔티티 user_id(pk)랑 OneToOne
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Column(name = "refresh_token",nullable = false)
    public String refreshToken;


    public RefreshToken(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }


    //refresh 토큰 업데이트 할때 쓰는 메소드(리프레시 토큰을 가지고 있는 경우)
    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}
