package com.nailshop.nailborhood.controller.search;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewReportListLookupDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.service.review.admin.ReviewReportStatusAdminService;
import com.nailshop.nailborhood.service.search.AdminSearchService;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/admin/search/review")
    public String searchReviewInquiry(Model model,
                                      @RequestParam(value = "keyword",required = false) String keyword,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                      @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {
        try {
            CommonResponseDto<Object> allReviewList = adminSearchService.searchReviewInquiry(keyword, page, size, sortBy);
            model.addAttribute("reviewList", allReviewList.getData());
            return "admin/admin_review_list";
        } catch (NotFoundException e) {
            //TODO: errorcode 마다 페이지 반환을 다르게 해줘야하는지 고민
            model.addAttribute("errorCode", ErrorCode.REVIEW_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_review_list";
        }
    }




    // 아트판 검색

    @Tag(name = "search", description = "search API")
    @Operation(summary = "아트 검색", description = "search API")
    @GetMapping("/admin/search/artRef")
    public ResponseEntity<ResultDto<ArtListResponseDto>> searchArtRefInquiry(@RequestHeader(AUTH) String accessToken,
                                                                             @RequestParam(value = "keyword") String keyword,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                             @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {
        CommonResponseDto<Object> commonResponseDto = adminSearchService.searchArtRefInquiry(accessToken, keyword, page, size, sortBy);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        resultDto.setData((ArtListResponseDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }

    // 매장 검색

    @GetMapping("/admin/search/shop")
    public String searchShopInquiry(Model model,
                                    //@RequestHeader(AUTH) String accessToken,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {

        try {
            CommonResponseDto<Object> allShopList = adminSearchService.searchShopInquiry(keyword, page, size, sortBy);
            model.addAttribute("shopList", allShopList.getData());
            return "admin/admin_shop_list";
        } catch (NotFoundException e) {
            //TODO: errorcode 마다 페이지 반환을 다르게 해줘야하는지 고민
            model.addAttribute("errorCode", ErrorCode.SHOP_NOT_FOUND);
            //model.addAttribute("errorCode" , ErrorCode.MEMBER_NOT_FOUND);
            return "admin/admin_shop_list";
        }
    }


}
