package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.CheckDto;
import com.nailshop.nailborhood.dto.member.SignUpDto;
import com.nailshop.nailborhood.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

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

}
