package com.nailshop.nailborhood.controller.shop;


import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.artboard.ArtDetailResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.home.HomeDetailResponseDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.alarm.AlarmService;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.service.shop.ShopArtBoardListService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.service.shop.ShopListLookupLocalService;
import com.nailshop.nailborhood.service.shop.ShopReviewListLookupService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.service.shop.owner.ShopRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ShopListLookupLocalService shopListLookupLocalService;
    private final ShopDetailService shopDetailService;
    private final ShopReviewListLookupService shopReviewListLookupService;
    private final ShopArtBoardListService shopArtBoardListService;
    private final ShopRegistrationService shopRegistrationService;
    private final MemberService memberService;
    private final AlarmService alarmService;
    private final CategoryRepository categoryRepository;



    // main
    @GetMapping(value = "/")

    public String getAllShops(@AuthenticationPrincipal MemberDetails memberDetails,
                              Model model,
                              Authentication authentication) {

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }

        CommonResponseDto<Object> allResultList = shopListLookupLocalService.getHome();
        ResultDto<HomeDetailResponseDto> resultDto = ResultDto.in(allResultList.getStatus(), allResultList.getMessage());
        resultDto.setData((HomeDetailResponseDto) allResultList.getData());
        model.addAttribute("resultDto", resultDto);

        return "home/home";

    }

    // 전체 매장 조회
    @GetMapping(value = "/shopList")
    public ResponseEntity<ResultDto<ShopListResponseDto>> getAllShops(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                      @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                      @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> allShopsList = shopListLookupLocalService.getShopList(page, size, sort, criteria);
        ResultDto<ShopListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
        resultDto.setData((ShopListResponseDto) allShopsList.getData());

        return ResponseEntity.status(allShopsList.getHttpStatus())
                             .body(resultDto);
    }

    // 내 주변 매장 전체 조회
    @GetMapping(value = "/shop/list")
    public String getAllShopListByDong(@RequestParam(value = "cityId", required = false) Long cityId,
                                       @RequestParam(value = "districtsId", required = false) Long districtsId,
                                       @RequestParam(value = "dongId", required = false) Long dongId,
                                       @RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                       @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                       @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                       Model model,
                                       @AuthenticationPrincipal MemberDetails memberDetails,
                                       Authentication authentication) {
//        dongId = 1L;
        try {
            if(authentication != null) {
                SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
                model.addAttribute("sessionDto", sessionDto);
            }

            CommonResponseDto<Object> allShopsList = shopListLookupLocalService.getShopListByDong(keyword, page, size, criteria, dongId, districtsId, cityId);
            ResultDto<ShopListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
            resultDto.setData((ShopListResponseDto) allShopsList.getData());

            StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();
            List<Map<String, String>> criteriaOptions = shopListLookupLocalService.createCriteriaOptions();


            model.addAttribute("resultDto", resultDto);
            model.addAttribute("addressDto", storeAddressSeparationListDtoList);
            model.addAttribute("criteriaOptions", criteriaOptions);
            model.addAttribute("size",size);
            model.addAttribute("orderby",criteria);
            model.addAttribute("keyword",keyword);

            return "shop/shop_local_list";
        } catch (NotFoundException e) {
            StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();
            List<Map<String, String>> criteriaOptions = shopListLookupLocalService.createCriteriaOptions();

            model.addAttribute("addressDto", storeAddressSeparationListDtoList);
            model.addAttribute("criteriaOptions", criteriaOptions);
            model.addAttribute("errorCode", ErrorCode.SHOP_NOT_FOUND);
            model.addAttribute("size",size);
            model.addAttribute("orderby",criteria);
            model.addAttribute("keyword",keyword);

            return "shop/shop_local_list";
        }

    }


    // 매장 상세 조회
    @GetMapping("/shopDetail/{shopId}")
    public String getShopDetail(Model model,
                                Authentication authentication,
                                @AuthenticationPrincipal MemberDetails memberDetails,
                                @PathVariable Long shopId) {

        // 결과 데이터 처리
        try {
            if(authentication != null) {
                SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
                model.addAttribute("sessionDto", sessionDto);
                CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetail(shopId, sessionDto.getId());
                ResultDto<ShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
                resultDto.setData((ShopDetailListResponseDto) shopDetail.getData());
                model.addAttribute("resultDto", resultDto);
            } else {
                CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetailForGuest(shopId);
                ResultDto<ShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
                resultDto.setData((ShopDetailListResponseDto) shopDetail.getData());
                model.addAttribute("resultDto", resultDto);
                model.addAttribute("sessionDto", "");
            }
            // 알람
            Member receiver = alarmService.getOwnerInfo(shopId);
            model.addAttribute("receiver", receiver);
//            model.addAttribute("isLoggedIn", isLoggedIn);
            return "shop/shop_detail";
        } catch (NotFoundException e) {

            model.addAttribute("errorCode", ErrorCode.SHOP_NOT_FOUND);
            model.addAttribute("ReviewErrorCode", ErrorCode.REVIEW_NOT_REGISTRATION);
            model.addAttribute("ArtErrorCode", ErrorCode.ART_NOT_REGISTRATION);
            return "shop/shop_detail";
        }
    }


    //매장 리뷰 조회
    @GetMapping("/review/{shopId}")
    public String getShopReviewList(Model model,
                                    Authentication authentication,
                                    @AuthenticationPrincipal MemberDetails memberDetails,
                                    @PathVariable Long shopId,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                    @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                    @RequestParam(value = "orderby", defaultValue = "likeCnt", required = false) String criteria,
                                    @RequestParam(value = "keyword", required = false) String keyword) {

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        boolean error = false;

        try {
            CommonResponseDto<Object> shopReview = shopReviewListLookupService.getSearchReviewListByShopId(page, size, category, criteria, keyword , shopId);

            List<Map<String, String>> criteriaOptions = shopReviewListLookupService.createCriteriaOptions();
            List<Category> categoryList = categoryRepository.findAll();

            model.addAttribute("shopReview", shopReview.getData());
            model.addAttribute("orderby", criteria);
            model.addAttribute("size", size);
            model.addAttribute("criteriaOptions", criteriaOptions);
            model.addAttribute("categories", categoryList);
            model.addAttribute("error", error);

        } catch (NotFoundException e) {
            error = true;
            model.addAttribute("error", error);
        }

        return "shop/shop_review_list";
    }


    // 매장 아트 조회
    @GetMapping("/art/{shopId}")
    public String getShopArtList(Model model,
                                 Authentication authentication,
                                 @AuthenticationPrincipal MemberDetails memberDetails,
                                 @PathVariable Long shopId,
                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                 @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                 @RequestParam(value = "sortBy", defaultValue = "likeCount", required = false) String criteria,
                                 @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                 @RequestParam(value = "keyword", required = false) String keyword) {

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        boolean error = false;

        try {
            CommonResponseDto<Object> shopArt = shopArtBoardListService.getAllArtBoardListByShopIdByCategory(page, size, criteria, category, keyword, shopId);
            ResultDto<ShopArtBoardListLookupResponseDto> resultDto = ResultDto.in(shopArt.getStatus(), shopArt.getMessage());
            resultDto.setData((ShopArtBoardListLookupResponseDto) shopArt.getData());

            List<Map<String, String>> criteriaOptions = shopArtBoardListService.createCriteriaOptions();
            List<Category> categoryList = categoryRepository.findAll();

            model.addAttribute("result", resultDto);
            model.addAttribute("error", error);
            model.addAttribute("sortBy", criteria);
            model.addAttribute("size", size);
            model.addAttribute("criteriaOptions", criteriaOptions);
            model.addAttribute("categories", categoryList);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {

            error = true;
            model.addAttribute(error);
        }

        return "shop/shop_art_list";
    }

    // 매장 아트 조회 카테고리
    @GetMapping("/art/category/{shopId}")
    public ResponseEntity<ResultDto<ShopArtBoardListLookupResponseDto>> getShopArtListByCategory(@PathVariable Long shopId,
                                                                        @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                        @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                                                        @RequestParam(value = "sortBy", defaultValue = "likeCount", required = false) String criteria,
                                                                        @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                                                        @RequestParam(value = "keyword", required = false) String keyword) {
        CommonResponseDto<Object> shopArt = shopArtBoardListService.getAllArtBoardListByShopIdByCategory(page, size, criteria, category, keyword, shopId);
        ResultDto<ShopArtBoardListLookupResponseDto> resultDto = ResultDto.in(shopArt.getStatus(), shopArt.getMessage());
        resultDto.setData((ShopArtBoardListLookupResponseDto) shopArt.getData());

        return ResponseEntity.status(shopArt.getHttpStatus()).body(resultDto);
    }


    // 매장 리뷰 조회 카테고리
    @GetMapping("/review/category/{shopId}")
    public ResponseEntity<ResultDto<ShopReviewListResponseDto>> getShopReviewListByCategory(@PathVariable Long shopId,
                                                                                        @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                        @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                                                                        @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria, @RequestParam(value = "category", defaultValue = "", required = false) String category, @RequestParam(value = "keyword", required = false) String keyword){
        CommonResponseDto<Object> shopReview = shopReviewListLookupService.getSearchReviewListByShopId(page, size, category, criteria, keyword , shopId);
        ResultDto<ShopReviewListResponseDto> resultDto = ResultDto.in(shopReview.getStatus(), shopReview.getMessage());
        resultDto.setData((ShopReviewListResponseDto) shopReview.getData());

        return ResponseEntity.status(shopReview.getHttpStatus()).body(resultDto);
    }
}
