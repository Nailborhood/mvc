package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.dto.member.request.*;

import com.nailshop.nailborhood.service.member.MemberService;
import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @Hidden
    @GetMapping("/")
    public ResponseEntity<?> logoutTest() {
        return ResponseEntity.status(200).body("로그아웃 완료");
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "member/login_form";
    }

    // 회원가입 페이지
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
    public ResponseEntity<ResultDto<DuplicationCheckDto>> emailOverlapCheck(@RequestBody DuplicationCheckDto duplicationCheckDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkEmailIsAvailable(duplicationCheckDto);
        ResultDto<DuplicationCheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((DuplicationCheckDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<ResultDto<DuplicationCheckDto>> nicknameOverlapCheck(@RequestBody DuplicationCheckDto duplicationCheckDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkNicknameIsAvailable(duplicationCheckDto);
        ResultDto<DuplicationCheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((DuplicationCheckDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @GetMapping("/checkPhoneNum")
    public ResponseEntity<ResultDto<DuplicationCheckDto>> phoneNumOverlapCheck(@RequestBody DuplicationCheckDto duplicationCheckDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkPhoneNumIsAvailable(duplicationCheckDto);
        ResultDto<DuplicationCheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((DuplicationCheckDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }


    @PostMapping("/renewToken")
    public ResponseEntity<?> renewToken (@CookieValue("refreshToken") String refreshToken) {
        CommonResponseDto<Object> commonResponseDto = memberService.renewToken(refreshToken);
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @PostMapping("/dropout")
    public ResponseEntity<ResultDto<Void>> memberDropOut(@RequestHeader(AUTH) String accessToken){
        CommonResponseDto<Object> commonResponseDto = memberService.deleteMember(accessToken);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }



}
