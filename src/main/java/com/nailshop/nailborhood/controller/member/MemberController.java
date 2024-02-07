package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.CheckDto;
import com.nailshop.nailborhood.dto.member.LoginDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import com.nailshop.nailborhood.dto.member.SignUpDto;
import com.nailshop.nailborhood.security.dto.TokenResponseDto;
import com.nailshop.nailborhood.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final String AUTH = HttpHeaders.AUTHORIZATION;

    @GetMapping("/checkEmail")
    public ResponseEntity<ResultDto<CheckDto>> emailOverlapCheck(@RequestBody CheckDto checkDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkEmailIsAvailable(checkDto);
        ResultDto<CheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((CheckDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<ResultDto<CheckDto>> nicknameOverlapCheck(@RequestBody CheckDto checkDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkNicknameIsAvailable(checkDto);
        ResultDto<CheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((CheckDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @GetMapping("/checkPhoneNum")
    public ResponseEntity<ResultDto<CheckDto>> phoneNumOverlapCheck(@RequestBody CheckDto checkDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkPhoneNumIsAvailable(checkDto);
        ResultDto<CheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((CheckDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @PostMapping("/signupProc")
    public ResponseEntity<ResultDto<Void>> signUp(@RequestBody SignUpDto signUpDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.signUp(signUpDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    @PostMapping("/loginProc")
    public ResponseEntity<ResultDto<TokenResponseDto>> login(@RequestBody LoginDto loginDto){
        CommonResponseDto<Object> commonResponseDto = memberService.memberLogin(loginDto);
        ResultDto<TokenResponseDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((TokenResponseDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .header(HttpHeaders.AUTHORIZATION, ((TokenResponseDto) commonResponseDto.getData()).getAccessToken())
                .body(result);
    }

    @GetMapping("myPage/myProfile")
    public ResponseEntity<ResultDto<MemberInfoDto>> getMyProfile(@RequestHeader(AUTH) String accessToken) {
        CommonResponseDto<Object> commonResponseDto = memberService.findMyInfo(accessToken);
        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((MemberInfoDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

//    @PostMapping("/renewToken")
//    public ResponseEntity<?> renewToken(@RequestHeader(AUTH) String accessToken) {
//        CommonResponseDto<Object> commonResponseDto = memberService.renewToken()
//        return ResponseEntity.status()
//    }



}
