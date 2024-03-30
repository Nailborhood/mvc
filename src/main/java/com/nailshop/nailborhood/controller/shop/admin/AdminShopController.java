package com.nailshop.nailborhood.controller.shop.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.exception.NotFoundException;
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

    // 매장 신청 조회
    @GetMapping(value = "/admin/search/shop/request")
    public String getAllShops(Model model,
                              //@RequestHeader(AUTH) String accessToken,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                              @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                              @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                              @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {


        try {
            CommonResponseDto<Object> allShopRequestList = shopRequestLookupAdminService.getAllShopRequest(keyword, page, size, criteria, sort);
            model.addAttribute("requestList", allShopRequestList.getData());
            return "admin/admin_shop_registration_list";
        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.SHOP_REQUEST_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_shop_registration_list";
        }
    }

    // 매장 신청 상세 조회
    @GetMapping("/shopRegistrationDetail/{shopId}")
    public String getShopDetail(Model model,
                                @PathVariable Long shopId){
        CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetail(shopId);

        model.addAttribute("shopDetail", shopDetail.getData());

        return "admin/admin_shop_registration";
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 삭제", description = "admin API")

    // 매장 삭제
    @PostMapping("/admin/delete/shop")
    public String deleteShop(@RequestParam Long shopId) {
         shopDeleteService.deleteShop(shopId);

        return "redirect:/admin/search/shop";
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 상태 변경 ", description = "admin API")
    // 매장 상태 변경
    @PutMapping("/admin/shopStatus/{shopId}")
    public ResponseEntity<ResultDto<Void>> changeReviewReportStatus(@RequestHeader(AUTH) String accessToken,
                                                                    @PathVariable Long shopId,
                                                                    @RequestParam(value = "status") String status) {
        CommonResponseDto<Object> commonResponseDto = shopStatusChangeService.changeShopStatus(accessToken, shopId, status);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장등록신청 승인", description = "admin API")
    @PutMapping("/admin/shop/approve/{shopId}")
    public ResponseEntity<ResultDto<Void>> shopApprove(/*@RequestHeader(AUTH) String accessToken,*/
                                                       @PathVariable Long shopId){
        CommonResponseDto<Object> shopApprove = shopRegistrationHandler.shopApprove(/*accessToken, */shopId);
        ResultDto<Void> resultDto = ResultDto.in(shopApprove.getStatus(), shopApprove.getMessage());

        return ResponseEntity.status(shopApprove.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장등록신청 거절", description = "admin API")
    @DeleteMapping("/admin/shop/reject/{shopId}")
    public ResponseEntity<ResultDto<Void>> shopReject(/*@RequestHeader(AUTH) String accessToken,*/
                                                      @PathVariable Long shopId){
        CommonResponseDto<Object> shopReject = shopRegistrationHandler.shopReject(/*accessToken, */shopId);
        ResultDto<Void> resultDto = ResultDto.in(shopReject.getStatus(), shopReject.getMessage());

        return ResponseEntity.status(shopReject.getHttpStatus()).body(resultDto);
    }
}
