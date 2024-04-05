package com.nailshop.nailborhood.controller.search;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.review.response.admin.AdminReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.review.admin.ReviewReportStatusAdminService;
import com.nailshop.nailborhood.service.search.AdminSearchService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RequiredArgsConstructor
@Controller
public class AdminSearchController {

    private final AdminSearchService adminSearchService;
    private final ReviewReportStatusAdminService reviewReportStatusAdminService;


    // 리뷰 검색
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/search/review")
    public String searchReviewInquiry(Model model,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                      @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {
        try {
            CommonResponseDto<Object> allReviewList = adminSearchService.searchReviewInquiry(keyword, page, size, sortBy);
            ResultDto<AdminReviewListResponseDto> resultDto = ResultDto.in(allReviewList.getStatus(), allReviewList.getMessage());
            resultDto.setData((AdminReviewListResponseDto) allReviewList.getData());
            model.addAttribute("resultDto", resultDto);
            return "admin/admin_review_list";
        } catch (NotFoundException e) {
            //TODO: errorcode 마다 페이지 반환을 다르게 해줘야하는지 고민
            model.addAttribute("errorCode", ErrorCode.REVIEW_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_review_list";
        }
    }


    // 매장 검색
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/search/shop")
    public String searchShopInquiry(Model model,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {

        try {
            CommonResponseDto<Object> allShopList = adminSearchService.searchShopInquiry(keyword, page, size, sortBy);
            ResultDto<AllShopsListResponseDto> resultDto = ResultDto.in(allShopList.getStatus(), allShopList.getMessage());
            resultDto.setData((AllShopsListResponseDto) allShopList.getData());
            model.addAttribute("resultDto", resultDto);
            return "admin/admin_shop_list";
        } catch (NotFoundException e) {
            //TODO: errorcode 마다 페이지 반환을 다르게 해줘야하는지 고민
            model.addAttribute("errorCode", ErrorCode.SHOP_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_shop_list";
        }
    }


}
