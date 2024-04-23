package com.nailshop.nailborhood.controller.shop.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.home.HomeDetailResponseDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.member.admin.ShopRegistrationHandler;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.service.shop.admin.ShopDeleteService;
import com.nailshop.nailborhood.service.shop.admin.ShopRequestLookupAdminService;
import com.nailshop.nailborhood.service.shop.admin.ShopStatusChangeService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class AdminShopController {


    private final ShopDeleteService shopDeleteService;
    private final ShopStatusChangeService shopStatusChangeService;
    private final ShopRequestLookupAdminService shopRequestLookupAdminService;
    private final ShopRegistrationHandler shopRegistrationHandler;
    private final ShopDetailService shopDetailService;
    private final MemberService memberService;

    // 매장 신청 조회
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin/search/shop/request")
    public String getAllShops(Model model,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                              @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                              @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                              @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {


        try {
            CommonResponseDto<Object> allShopRequestList = shopRequestLookupAdminService.getAllShopRequest(keyword, page, size, criteria, sort);
            ResultDto<AllShopsListResponseDto> resultDto = ResultDto.in(allShopRequestList.getStatus(), allShopRequestList.getMessage());
            resultDto.setData((AllShopsListResponseDto) allShopRequestList.getData());
            model.addAttribute("resultDto" ,resultDto);

            return "admin/admin_shop_registration_list";
        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.SHOP_REQUEST_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_shop_registration_list";
        }
    }

    // 매장 신청 상세 조회
    // TODO 매장상세에 heartStatus때문에 memberDetails 추가함
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/shopRegistrationDetail/{shopId}")
    public String getShopDetail(Authentication authentication,
                                @AuthenticationPrincipal MemberDetails memberDetails,
                                Model model,
                                @PathVariable Long shopId){
        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetail(shopId, sessionDto.getId());

        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";

        model.addAttribute("shopDetail", shopDetail.getData());
        model.addAttribute("memberNickname", nicknameSpace);

        return "admin/admin_shop_registration";
    }




    // 매장 삭제
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/delete/shop")
    public String deleteShop(@RequestParam Long shopId) {
         shopDeleteService.deleteShop(shopId);

        return "redirect:/admin/search/shop";
    }


    // 매장 상태 변경
/*    @PutMapping("/admin/shopStatus/{shopId}")
    public ResponseEntity<ResultDto<Void>> changeReviewReportStatus(@RequestHeader(AUTH) String accessToken,
                                                                    @PathVariable Long shopId,
                                                                    @RequestParam(value = "status") String status) {
        CommonResponseDto<Object> commonResponseDto = shopStatusChangeService.changeShopStatus(accessToken, shopId, status);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }*/

    // 매장등록신청 승인
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/shop/approve/{shopId}")
    public ResponseEntity<ResultDto<Void>> shopApprove(@AuthenticationPrincipal MemberDetails memberDetails,
                                                       @PathVariable Long shopId){
        CommonResponseDto<Object> shopApprove = shopRegistrationHandler.shopApprove(memberDetails, shopId);
        ResultDto<Void> resultDto = ResultDto.in(shopApprove.getStatus(), shopApprove.getMessage());

        return ResponseEntity.status(shopApprove.getHttpStatus()).body(resultDto);
    }

    // 매장등록신청 거절
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/shop/reject/{shopId}")
    public ResponseEntity<ResultDto<Void>> shopReject(@AuthenticationPrincipal MemberDetails memberDetails,
                                                      @PathVariable Long shopId){
        CommonResponseDto<Object> shopReject = shopRegistrationHandler.shopReject(memberDetails, shopId);
        ResultDto<Void> resultDto = ResultDto.in(shopReject.getStatus(), shopReject.getMessage());

        return ResponseEntity.status(shopReject.getHttpStatus()).body(resultDto);
    }
}
