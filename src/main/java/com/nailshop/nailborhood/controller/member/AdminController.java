package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.member.admin.MemberInquiryService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberInquiryService memberInquiryService;
    private final MemberService memberService;


    // 유저 전체 조회
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/search/member")
    public String inquiryAllMember(Model model,
                                   @RequestParam(value = "keyword",required = false) String keyword,
                                   @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
                                   Authentication authentication,
                                   @AuthenticationPrincipal MemberDetails memberDetails) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);
        try {
            CommonResponseDto<Object> AllMemberList = memberInquiryService.inquiryAllMember(keyword,page, size, sortBy);
            ResultDto<MemberListResponseDto> resultDto = ResultDto.in(AllMemberList.getStatus(), AllMemberList.getMessage());
            resultDto.setData((MemberListResponseDto) AllMemberList.getData());
            model.addAttribute("resultDto" ,resultDto);
            model.addAttribute("size",size);
            model.addAttribute("sortBy",sortBy);
            model.addAttribute("keyword",keyword);
            return "admin/admin_member_list";
        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.MEMBER_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_member_list";
        }


    }


}
