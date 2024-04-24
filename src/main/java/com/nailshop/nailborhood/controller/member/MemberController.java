package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.dto.member.request.*;

import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 로그인 페이지
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String loginPage() {
        return "member/login_form";
    }

    // 회원가입 페이지
    @PreAuthorize("isAnonymous()")
    @GetMapping("/signup")
    public String signupPage() {
        return "member/signup_form";
    }

    @PostMapping("/signupProc")
    public String signupProc(SignUpRequestDto signUpRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.signUp(signUpRequestDto);
        System.out.println(commonResponseDto.getMessage());
        return "redirect:/login";
    }

    @GetMapping("/checkEmail")
    @ResponseBody
    public Boolean emailOverlapCheck(@RequestParam("email") String email) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkEmailIsAvailable(email);
        ResultDto<DuplicationCheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((DuplicationCheckDto) commonResponseDto.getData());
        return result.getData().isExist();
    }
    @GetMapping("/checkNickname")
    @ResponseBody
    public Boolean nicknameOverlapCheck(@RequestParam("nickname") String nickname) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkNicknameIsAvailable(nickname);
        ResultDto<DuplicationCheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((DuplicationCheckDto) commonResponseDto.getData());
        return result.getData().isExist();
    }

    @GetMapping("/checkPhoneNum")
    @ResponseBody
    public Boolean phoneNumOverlapCheck(@RequestBody DuplicationCheckDto duplicationCheckDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkPhoneNumIsAvailable(duplicationCheckDto);
        ResultDto<DuplicationCheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((DuplicationCheckDto) commonResponseDto.getData());
        return result.getData().isExist();
    }


    // 마이페이지 겸 유저 조회 페이지로...?
//    @GetMapping("/user")
    @GetMapping("/user/{memberId}")
    public String userPage(Authentication authentication,
                           @AuthenticationPrincipal MemberDetails memberDetails,
                           @PathVariable(name = "memberId") Long memberId,
                           Model model) {
        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        CommonResponseDto<Object> commonResponseDto = memberService.findUser(memberId);
        model.addAttribute("userInfoResponseDto", commonResponseDto.getData());
        return "mypage/user";
    }

}
