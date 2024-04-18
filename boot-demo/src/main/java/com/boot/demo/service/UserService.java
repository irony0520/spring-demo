package com.boot.demo.service;

import com.boot.demo.dto.LoginDto;
import com.boot.demo.dto.TokenRequest;
import com.boot.demo.dto.TokenResponse;
import com.boot.demo.dto.UserFormDto;
import com.boot.demo.entity.RefreshToken;
import com.boot.demo.entity.User;
import com.boot.demo.jwt.TokenProvider;
import com.boot.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;


    public boolean validateUser(UserFormDto dto) {
        User existUser = userRepository.findById(dto.getId()).orElse(null);

        return existUser == null;
    }

    public void signUp(UserFormDto dto) {

        if(!validateUser(dto)) throw new RuntimeException("이미 존재하는 아이디입니다.");
        //entity생성
        User saveUser = User.createUser(dto,passwordEncoder);
        userRepository.save(saveUser);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findById(username)
                .orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
    }

    public TokenResponse login(LoginDto dto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getId(), dto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //1. 해당 유저 조회.
        User user = userRepository.findById(authentication.getName()).orElseThrow(EntityNotFoundException::new);
        // 2. 해당 유저에 매핑된 리프레시 토큰 조회
        String newRefreshToken = tokenProvider.createRefreshToken(Duration.ofDays(1));
        RefreshToken existRefreshToken = refreshTokenService.findByUser(user);

        if(existRefreshToken==null) {
            //2-1. 토큰이 없을시 생성 후 저장
            refreshTokenService.saveToken(new RefreshToken(user,newRefreshToken));
        }else{
            //2-2. 이미 존재할땐 새로 발급(업데이트)
            existRefreshToken.update(newRefreshToken);
        }

        //3. 액세스 토큰 발급 TokenResponse(액세스 , 리프레시 토큰 둘다 반환)
        String accessToken = tokenProvider.createAccessToken(user,Duration.ofHours(2));
        return new TokenResponse(accessToken, newRefreshToken);
    }

    public TokenResponse tokenRefresh(TokenRequest request) throws Exception{
        if(!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("Unexpected Token");
        }
        //리프레시 토큰이 정상적인경우
        RefreshToken refreshToken = refreshTokenService
                .findByRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        String accessToken = tokenProvider.createAccessToken(user, Duration.ofHours(2));
        //토큰 두개 다 새로 생성 바뀌는 리프레시 토큰을 getRefreshToken으로 꺼내옴
        String newRefreshToken = refreshToken.update(tokenProvider.createRefreshToken(Duration.ofDays(1))).getRefreshToken();

        //2개 다 새로 갱신
        return new TokenResponse(accessToken, newRefreshToken);
    }
}


