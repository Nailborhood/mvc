package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.dto.member.request.*;
import com.nailshop.nailborhood.security.dto.TokenResponseDto;
import com.nailshop.nailborhood.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailborhood")
public class MemberController {
    private final MemberService memberService;



    @GetMapping("/")
    public ResponseEntity<?> main() {
        return ResponseEntity.status(200).body("로그아웃 완료");
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

    @PostMapping("/signupProc")
    public ResponseEntity<ResultDto<Void>> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.signUp(signUpRequestDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    @PostMapping("/loginProc")
    public ResponseEntity<ResultDto<?>> login(@RequestBody LoginRequestDto loginRequestDto){
        CommonResponseDto<Object> commonResponseDto = memberService.memberLogin(loginRequestDto);
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
    public ResponseEntity<ResultDto<MemberInfoDto>> modMyInfo(@RequestHeader(AUTH) String accessToken,
                                       @RequestBody ModMemberInfoRequestDto modMemberInfoRequestDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.updateMyInfo(accessToken, modMemberInfoRequestDto);
        ResultDto<MemberInfoDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((MemberInfoDto) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultDto<Object>> logout(@RequestHeader(AUTH) String accessToken){
        CommonResponseDto<Object> commonResponseDto = memberService.logout(accessToken);
        HttpHeaders newHeader = new HttpHeaders();
        newHeader.add(HttpHeaders.SET_COOKIE, "cookieName=refreshToken=" + "; Path=/; Max-Age=0");
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                .headers(newHeader)
                .body(result);
    }

    @PostMapping("/myPage/passwordCheck")
    public ResponseEntity<ResultDto<Object>> passwordCheck(@RequestHeader(AUTH) String accessToken,
                                           @RequestBody BeforeModPasswordCheckRequestDto beforeModPasswordCheckRequestDto){
        CommonResponseDto<Object> commonResponseDto = memberService.beforeUpdatePassword(accessToken, beforeModPasswordCheckRequestDto);
        ResultDto<Object> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((boolean) commonResponseDto.getData());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @PostMapping("/myPage/modifyPassword")
    public ResponseEntity<ResultDto<Void>> modifyPassword(@RequestHeader(AUTH) String accessToken,
                                           @RequestBody ModPasswordRequestDto modPasswordRequestDto){
        CommonResponseDto<Object> commonResponseDto = memberService.updatePassword(accessToken, modPasswordRequestDto);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }

    @PostMapping("/dropout")
    public ResponseEntity<ResultDto<Void>> memberDropOut(@RequestHeader(AUTH) String accessToken){
        CommonResponseDto<Object> commonResponseDto = memberService.deleteMember(accessToken);
        ResultDto<Void> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }



}
