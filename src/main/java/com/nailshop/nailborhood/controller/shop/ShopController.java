package com.nailshop.nailborhood.controller.shop;

import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.home.HomeDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopReviewListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.dto.shop.response.StoreAddressSeparationListDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
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


    // main
    @GetMapping(value = "/")
    public String getAllShops(/*@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                      @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                      @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort*/
            Model model) {


        CommonResponseDto<Object> allResultList = shopListLookupLocalService.getHome();
        ResultDto<HomeDetailResponseDto> resultDto = ResultDto.in(allResultList.getStatus(), allResultList.getMessage());
        resultDto.setData((HomeDetailResponseDto) allResultList.getData());
        model.addAttribute("resultDto", resultDto);
        //TODO: main view 만들기
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
                                       @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort,
                                       Model model) {
//        dongId = 1L;
        try {
            CommonResponseDto<Object> allShopsList = shopListLookupLocalService.getShopListByDong(keyword, page, size, sort, criteria, dongId, districtsId, cityId);
            ResultDto<ShopListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
            resultDto.setData((ShopListResponseDto) allShopsList.getData());

            StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();
            List<Map<String, String>> criteriaOptions = shopListLookupLocalService.createCriteriaOptions();

            model.addAttribute("resultDto", resultDto);
            model.addAttribute("addressDto", storeAddressSeparationListDtoList);
            model.addAttribute("criteriaOptions", criteriaOptions);
            return "shop/shop_local_list";
        } catch (NotFoundException e) {
            StoreAddressSeparationListDto storeAddressSeparationListDtoList = shopRegistrationService.findAddress();
            List<Map<String, String>> criteriaOptions = shopListLookupLocalService.createCriteriaOptions();

            model.addAttribute("addressDto", storeAddressSeparationListDtoList);
            model.addAttribute("criteriaOptions", criteriaOptions);
            model.addAttribute("errorCode", ErrorCode.SHOP_NOT_FOUND);
            return "shop/shop_local_list";
        }

    }


    // 매장 상세 조회
    @GetMapping("/shopDetail/{shopId}")
    public String getShopDetail(Model model,
                                @AuthenticationPrincipal MemberDetails memberDetails,
                                @PathVariable Long shopId) {


        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember()
                                                                      .getNickname() : "";
        // 로그인 여부를 확인
        boolean isLoggedIn = memberDetails != null;


        CommonResponseDto<Object> shopDetail;

        if (isLoggedIn) {
            // 로그인한 경우
            shopDetail = shopDetailService.getShopDetail(shopId, memberDetails);
        } else {
            shopDetail = shopDetailService.getShopDetailForGuest(shopId);

        }
        // 결과 데이터 처리
        try {
            ResultDto<ShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
            resultDto.setData((ShopDetailListResponseDto) shopDetail.getData());
            model.addAttribute("memberNickname", nicknameSpace);
            model.addAttribute("isLoggedIn", isLoggedIn);
            model.addAttribute("resultDto", resultDto);
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
                                    @AuthenticationPrincipal MemberDetails memberDetails,
                                    @PathVariable Long shopId,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                    @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {

        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember()
                                                                      .getNickname() : "";
        model.addAttribute("memberNickname", nicknameSpace);
        boolean error = false;

        try {
            CommonResponseDto<Object> shopReview = shopReviewListLookupService.getAllReviewListByShopId(page, size, criteria, sort, shopId);
//        ResultDto<ShopReviewListResponseDto> resultDto = ResultDto.in(shopReview.getStatus(), shopReview.getMessage());
//        resultDto.setData((ShopReviewListResponseDto) shopReview.getData());

            model.addAttribute("shopReview", shopReview.getData());
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
                                 @PathVariable Long shopId,
                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                 @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                 @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                 @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> shopArt = shopArtBoardListService.getAllArtBoardListByShopId(page, size, criteria, sort, shopId);
        ResultDto<ShopArtBoardListLookupResponseDto> resultDto = ResultDto.in(shopArt.getStatus(), shopArt.getMessage());
        resultDto.setData((ShopArtBoardListLookupResponseDto) shopArt.getData());

        model.addAttribute("result", resultDto.getData());

        return "shop/shop_art_list";
    }
}
