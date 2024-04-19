package com.boot.demo.controller;

import com.boot.demo.dto.LoginDto;
import com.boot.demo.dto.TokenRequest;
import com.boot.demo.dto.TokenResponse;
import com.boot.demo.dto.UserFormDto;
import com.boot.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid UserFormDto dto) {
        try{
            userService.signUp(dto);
            return new ResponseEntity<>("회원가입이 완료됐습니다.", HttpStatus.CREATED);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid LoginDto dto) {

        try{
            return new ResponseEntity<>(userService.login(dto), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    //새로운 리프레시 토큰을 내려주는 역할
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid TokenRequest request) {
        try{
            return new ResponseEntity<>(userService.tokenRefresh(request),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid TokenRequest request) {
        try{
            userService.logout(request);
        }catch(Exception e) {
            log.info(e.getMessage());
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
