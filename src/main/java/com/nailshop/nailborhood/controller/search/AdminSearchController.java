package com.nailshop.nailborhood.controller.search;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.service.search.AdminSearchService;
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
@RequestMapping("/nailborhood")
public class AdminSearchController {

    private final AdminSearchService adminSearchService;

    // 회원 검색
    @Tag(name = "search", description = "admin API")
    @Operation(summary = "유저 검색(탈퇴 회원 포함)", description = "admin API")
    @GetMapping("/admin/search/member")
    public ResponseEntity<ResultDto<MemberListResponseDto>> inquiryAllMember(@RequestHeader(AUTH) String accessToken,
                                                                             @RequestParam(value = "keyword") String keyword,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                             @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy) {
        CommonResponseDto<Object> inquiryAllMember = adminSearchService.searchMemberInquiry(accessToken, keyword, page, size, sortBy);
        ResultDto<MemberListResponseDto> resultDto = ResultDto.in(inquiryAllMember.getStatus(), inquiryAllMember.getMessage());
        resultDto.setData((MemberListResponseDto) inquiryAllMember.getData());

        return ResponseEntity.status(inquiryAllMember.getHttpStatus())
                             .body(resultDto);
    }

    // 리뷰 검색
    @Tag(name = "search", description = "search API")
    @Operation(summary = "리뷰 검색", description = "search API")
    @GetMapping("/admin/search/review")
    public ResponseEntity<ResultDto<ReviewListResponseDto>> searchReviewInquiry(@RequestHeader(AUTH) String accessToken,
                                                                                @RequestParam(value = "keyword") String keyword,
                                                                                @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                                                                @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {
        CommonResponseDto<Object> commonResponseDto = adminSearchService.searchReviewInquiry(accessToken, keyword, page, size, sortBy);
        ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        resultDto.setData((ReviewListResponseDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
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
                                    @RequestParam(value = "keyword",required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy) {
        //TODO: auth 추가되면 변경
        //CommonResponseDto<Object> commonResponseDto = adminSearchService.searchShopInquiry(accessToken, keyword, page, size, sortBy);
        CommonResponseDto<Object> allShopsList = adminSearchService.searchShopInquiry( keyword, page, size, sortBy);
        model.addAttribute("shopList" , allShopsList.getData());
        return "admin/admin_shop_list";
    }


    //TODO: 채팅검색
}
