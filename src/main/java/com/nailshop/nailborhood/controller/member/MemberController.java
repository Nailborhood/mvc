package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.security.dto.TokenResponseDto;
import com.nailshop.nailborhood.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final String AUTH = HttpHeaders.AUTHORIZATION;



    @GetMapping("/")
    public ResponseEntity<?> main() {
        return ResponseEntity.status(200).body("로그아웃 완료");
    }


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
    public ResponseEntity<ResultDto<?>> login(@RequestBody LoginDto loginDto){
        CommonResponseDto<Object> commonResponseDto = memberService.memberLogin(loginDto);
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        Map<String, Object> resultMap = (Map<String, Object>) commonResponseDto.getData();
        TokenResponseDto tokenResponseDto = (TokenResponseDto) resultMap.get("accessToken");
        result.setData(tokenResponseDto);
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .header(HttpHeaders.AUTHORIZATION, tokenResponseDto.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, resultMap.get("refreshToken").toString())
                .body(result);
    }

    @GetMapping("myPage/myInfo")
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

    @PutMapping("/myPage/modMyInfo")
    public ResponseEntity<?> modMyInfo(@RequestHeader(AUTH) String accessToken,
                                       @RequestBody ModMemberInfoRequestDto modMemberInfoRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.updateMyInfo(accessToken, modMemberInfoRequestDto);
        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((MemberInfoDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTH) String accessToken){
        CommonResponseDto<Object> commonResponseDto = memberService.logout(accessToken);
        HttpHeaders newHeader = new HttpHeaders();
        newHeader.add(HttpHeaders.SET_COOKIE, "cookieName=refreshToken=" + "; Path=/; Max-Age=0");
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .headers(newHeader)
                .body(result);
    }

}
