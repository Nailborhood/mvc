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

    @GetMapping("/logout")
    public String logout(@RequestHeader(AUTH) String accessToken) {
        CommonResponseDto<Object> commonResponseDto = memberService.logout(accessToken);
        HttpHeaders newHeader = new HttpHeaders();
        newHeader.add(HttpHeaders.SET_COOKIE, "cookieName=refreshToken=" + "; Path=/; Max-Age=0");
        return "redirect:/login";
    }



    @PostMapping("/signupProc")
    public String signupProc(SignUpRequestDto signUpRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.signUp(signUpRequestDto);
//        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        System.out.println(commonResponseDto.getMessage());
//        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
        return "redirect:/login";
    }

//
//    @PostMapping(value = "/loginProc")
//    public void loginProc(@RequestParam("email") String email,
//                            @RequestParam("password") String password,
//                            HttpServletRequest request, HttpServletResponse response) {
//        CommonResponseDto<Object> commonResponseDto = memberService.memberLogin(email, password);
//
//        if(commonResponseDto.getHttpStatus().equals(HttpStatus.OK)) {
//            System.out.println("로그인 성공");
//        } else {
//            System.out.println("로그인 실패");
//            System.out.println(commonResponseDto.getMessage());
//        }
//    }

//    @PostMapping("/loginProc")
//    public ResponseEntity<ResultDto<?>> loginProc(@RequestBody LoginRequestDto loginRequestDto){
//        CommonResponseDto<Object> commonResponseDto = memberService.memberLogin(loginRequestDto);
//        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
//        Map<String, Object> resultMap = (Map<String, Object>) commonResponseDto.getData();
//        TokenResponseDto tokenResponseDto = (TokenResponseDto) resultMap.get("accessToken");
//        result.setData(tokenResponseDto);
//        return ResponseEntity.status(commonResponseDto.getHttpStatus())
//                .header(HttpHeaders.AUTHORIZATION, tokenResponseDto.getAccessToken())
//                .header(HttpHeaders.SET_COOKIE, resultMap.get("refreshToken").toString())
//                .body(result);
//    }


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


    @PostMapping("/logoutProc")
    public ResponseEntity<ResultDto<Object>> logoutProc(@RequestHeader(AUTH) String accessToken){
        CommonResponseDto<Object> commonResponseDto = memberService.logout(accessToken);
        HttpHeaders newHeader = new HttpHeaders();
        newHeader.add(HttpHeaders.SET_COOKIE, "cookieName=refreshToken=" + "; Path=/; Max-Age=0");
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .headers(newHeader)
                .body(result);
    }


    @PostMapping("/dropout")
    public ResponseEntity<ResultDto<Void>> memberDropOut(@RequestHeader(AUTH) String accessToken){
        CommonResponseDto<Object> commonResponseDto = memberService.deleteMember(accessToken);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }



}
