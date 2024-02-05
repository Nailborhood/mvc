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

    @GetMapping("/checkEmailAvailable")
    public ResponseEntity<ResultDto<CheckDto>> emailOverlapCheck(@RequestBody CheckDto checkDto) {
        CommonResponseDto<Object> commonResponseDto = memberService.checkEmailIsAvailable(checkDto);
        ResultDto<CheckDto> result = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        result.setData((CheckDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(result);
    }


}
