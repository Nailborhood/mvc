package com.nailshop.nailborhood.controller.shop;

import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.service.shop.ShopArtBoardListService;
import com.nailshop.nailborhood.service.shop.ShopDetailService;
import com.nailshop.nailborhood.service.shop.ShopListLookupLocalService;
import com.nailshop.nailborhood.service.shop.ShopReviewListLookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailborhood")
public class ShopController {

    private final ShopListLookupLocalService shopListLookupLocalService;
    private final ShopDetailService shopDetailService;
    private final ShopReviewListLookupService shopReviewListLookupService;
    private final ShopArtBoardListService shopArtBoardListService;

    @Tag(name = "Home", description = "Home API")
    @Operation(summary = "매장 전체 조회", description = "Home API")
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

    @Tag(name = "Local", description = "Local API")
    @Operation(summary = "내 주변 매장 전체 조회", description = "Local API")
    // 전체 매장 조회
    @GetMapping(value = "/shopList/{dongId}")
    public ResponseEntity<ResultDto<ShopListResponseDto>> getAllShopsbyDong(@PathVariable Long dongId,
                                                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                            @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                            @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> allShopsList = shopListLookupLocalService.getShopListByDong( page, size, sort, criteria, dongId);
        ResultDto<ShopListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
        resultDto.setData((ShopListResponseDto) allShopsList.getData());

        return ResponseEntity.status(allShopsList.getHttpStatus())
                             .body(resultDto);
    }

    @Tag(name = "ShopDetail", description = "Shop API")
    @Operation(summary = "매장 상세 조회", description = "Shop API")
    // 매장 상세 조회
    @GetMapping("/shopDetail/{shopId}")
    public ResponseEntity<ResultDto<ShopDetailListResponseDto>> getShopDetail(@PathVariable Long shopId){
        CommonResponseDto<Object> shopDetail = shopDetailService.getShopDetail(shopId);
        ResultDto<ShopDetailListResponseDto> resultDto = ResultDto.in(shopDetail.getStatus(), shopDetail.getMessage());
        resultDto.setData((ShopDetailListResponseDto) shopDetail.getData());

        return ResponseEntity.status(shopDetail.getHttpStatus()).body(resultDto);
    }


    @Tag(name = "ShopReview", description = "Shop API")
    @Operation(summary = "매장 리뷰 조회", description = "Shop API")
    // 매장 상세 조회
    @GetMapping("/review/{shopId}")
    public ResponseEntity<ResultDto<ShopReviewListLookupResponseDto>> getShopReviewList(@PathVariable Long shopId,
                                                                                        @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                        @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                                        @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                                        @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort){
        CommonResponseDto<Object> shopReview = shopReviewListLookupService.getAllReviewListByShopId(page,size,criteria,sort,shopId);
        ResultDto<ShopReviewListLookupResponseDto> resultDto = ResultDto.in(shopReview.getStatus(), shopReview.getMessage());
        resultDto.setData((ShopReviewListLookupResponseDto) shopReview.getData());

        return ResponseEntity.status(shopReview.getHttpStatus()).body(resultDto);
    }


    @Tag(name = "ShopArtBoard", description = "Shop API")
    @Operation(summary = "매장 아트판 조회", description = "Shop API")
    // 매장 상세 조회
    @GetMapping("/art/{shopId}")
    public ResponseEntity<ResultDto<ShopArtBoardListLookupResponseDto>> getShopArtList(@PathVariable Long shopId,
                                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                       @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                                       @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                                       @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort){
        CommonResponseDto<Object> shopArt = shopArtBoardListService.getAllArtBoardListByShopId(page,size,criteria,sort,shopId);
        ResultDto<ShopArtBoardListLookupResponseDto> resultDto = ResultDto.in(shopArt.getStatus(), shopArt.getMessage());
        resultDto.setData((ShopArtBoardListLookupResponseDto) shopArt.getData());

        return ResponseEntity.status(shopArt.getHttpStatus()).body(resultDto);
    }


}
