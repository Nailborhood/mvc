package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.service.member.admin.AppliedShopInquiryService;
import com.nailshop.nailborhood.service.member.admin.ShopRegistrationHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.member.admin.ChangeRoleService;
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

    private final ChangeRoleService changeRoleService;
    private final MemberInquiryService memberInquiryService;
    private final AppliedShopInquiryService appliedShopInquiryService;

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "사업자 전환", description = "admin API")
    @PutMapping("/admin/member/changeRole/{memberId}")
    public ResponseEntity<ResultDto<Void>> changeRole(@RequestHeader(AUTH) String accessToken,
                                                      @PathVariable Long memberId) {
        CommonResponseDto<Object> changeRole = changeRoleService.changeRole(accessToken, memberId);
        ResultDto<Void> resultDto = ResultDto.in(changeRole.getStatus(), changeRole.getMessage());

        return ResponseEntity.status(changeRole.getHttpStatus())
                             .body(resultDto);
    }

    // 유저 전체 조회
    @GetMapping("/admin/search/member")
    public String inquiryAllMember(Model model,
                                   @RequestParam(value = "keyword",required = false) String keyword,
                                   @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy) {

        try {
            CommonResponseDto<Object> AllMemberList = memberInquiryService.inquiryAllMember(keyword,page, size, sortBy);
            ResultDto<MemberListResponseDto> resultDto = ResultDto.in(AllMemberList.getStatus(), AllMemberList.getMessage());
            resultDto.setData((MemberListResponseDto) AllMemberList.getData());
            model.addAttribute("resultDto" ,resultDto);
            return "admin/admin_member_list";
        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.MEMBER_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_member_list";
        }


    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장등록신청 전체 조회", description = "admin API")
    @GetMapping("/admin/shop/inquiry")
    public ResponseEntity<ResultDto<ShopListResponseDto>> inquiryAllAppliedShop(@RequestHeader(AUTH) String accessToken,
                                                                                @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                                @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy) {
        CommonResponseDto<Object> inquiryAllAppliedShop = appliedShopInquiryService.inquiryAllAppliedShop(accessToken, page, size, sortBy);
        ResultDto<ShopListResponseDto> resultDto = ResultDto.in(inquiryAllAppliedShop.getStatus(), inquiryAllAppliedShop.getMessage());
        resultDto.setData((ShopListResponseDto) inquiryAllAppliedShop.getData());

        return ResponseEntity.status(inquiryAllAppliedShop.getHttpStatus()).body(resultDto);
    }
}
