package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.service.member.admin.ChangeRoleService;
import com.nailshop.nailborhood.service.member.admin.MemberInquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RestController
@RequiredArgsConstructor
@RequestMapping("nailborhood")
public class AdminController {

    private final ChangeRoleService changeRoleService;
    private final MemberInquiryService memberInquiryService;


    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "사업자 전환", description = "admin API")
    @PutMapping("/admin/member/changeRole/{memberId}")
    public ResponseEntity<ResultDto<Void>> changeRole (@RequestHeader(AUTH) String accessToken,
                                                       @PathVariable Long memberId){
        CommonResponseDto<Object> changeRole = changeRoleService.changeRole(memberId);
        ResultDto<Void> resultDto = ResultDto.in(changeRole.getStatus(), changeRole.getMessage());

        return ResponseEntity.status(changeRole.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "유저 전체 조회(탈퇴 회원 포함)", description = "admin API")
    @GetMapping("/admin/member/inquiry")
    public ResponseEntity<ResultDto<MemberListResponseDto>> inquiryAllMember(@RequestHeader(AUTH) String accessToken,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                             @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy){
        CommonResponseDto<Object> inquiryAllMember = memberInquiryService.inquiryAllMember(page, size, sortBy);
        ResultDto<MemberListResponseDto> resultDto = ResultDto.in(inquiryAllMember.getStatus(), inquiryAllMember.getMessage());
        resultDto.setData((MemberListResponseDto) inquiryAllMember.getData());

        return ResponseEntity.status(inquiryAllMember.getHttpStatus()).body(resultDto);
    }
}
