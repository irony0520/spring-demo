package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }


    //bindingResult - 스프링이 제공하는 검증 오류 보관 객체 bindingResult.hasErrors는 error유무를 판단
    @PostMapping("new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,Model model){

        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        try{
            Member member = Member.createMember(memberFormDto, new BCryptPasswordEncoder());
            memberService.saveMember(member);
        }catch(IllegalStateException e) {
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "login")
    public String loginMember() {
        return "member/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }

    @GetMapping("/delete")
    public String showDeleteMember(@RequestParam(value = "success", required = false) boolean success, Model model) {
        if (success) {
            model.addAttribute("message", "회원탈퇴가 완료되었습니다.");
        }
        return "member/memberDeleteForm";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam("email") String email, Model model) {
        Member member = memberService.findByEmail(email);
        if (member != null) {
            memberService.deleteMember(member);
            model.addAttribute("success", true); // 회원 탈퇴 성공 여부를 모델에 추가
            return "member/memberDeleteForm";
        } else {
            model.addAttribute("deleteErrorMsg", "해당 이메일로 등록된 회원이 없습니다.");
            return "member/memberDeleteForm";
        }
    }

}





