package com.nailshop.nailborhood.service.shop;

import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.MenuDetailResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopDetailLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.detail.ShopImgListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ShopDetailService {

    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final ShopImgRepository shopImgRepository;
    private final ReviewRepository reviewRepository;
    private final ShopReviewListLookupService shopReviewListLookupService;
    private final ShopArtBoardListService shopArtBoardListService;


    // 매장 상세 조회
    @Transactional
    public CommonResponseDto<Object> getShopDetail(Long shopId) {


        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
        ShopDetailLookupResponseDto shopDetailLookupResponseDto = Stream.of(shop)
                                                                        .map(s -> new ShopDetailLookupResponseDto(
                                                                                        s.getShopId(),
                                                                                        s.getName(),
                                                                                        s.getPhone(),
                                                                                        s.getAddress(),
                                                                                        s.getOpentime(),
                                                                                        s.getWebsite(),
                                                                                        s.getContent(),
                                                                                        s.getStatus(),
                                                                                        s.getCreatedAt(),
                                                                                        s.getRateAvg()
                                                                                )


                                                                        )
                                                                        .findFirst()
                                                                        .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));


        List<MenuDetailResponseDto> menuDetailResponseDtoList = menuRepository.findAllByShopId(shopId);
        List<ShopImgListResponseDto> shopImgListResponseDtoList = shopImgRepository.findAllByShopImgListByShopId(shopId);


        CommonResponseDto<Object> reviewList = shopReviewListLookupService.getAllReviewListByShopId(1, 4, "createdAt", "DESC", shopId);
        ResultDto<ShopReviewListLookupResponseDto> reviewResultDto = ResultDto.in(reviewList.getStatus(), reviewList.getMessage());
        reviewResultDto.setData((ShopReviewListLookupResponseDto) reviewList.getData());

        CommonResponseDto<Object> artList = shopArtBoardListService.getAllArtBoardListByShopId(1, 4, "createdAt", "DESC", shopId);
        ResultDto<ShopArtBoardListLookupResponseDto> artBoardResultDto = ResultDto.in(artList.getStatus(), artList.getMessage());
        artBoardResultDto.setData((ShopArtBoardListLookupResponseDto) artList.getData());

        ShopDetailListResponseDto shopDetailListResponseDto = ShopDetailListResponseDto.builder()
                                                                                       .shopDetailLookupResponseDto(shopDetailLookupResponseDto)
                                                                                       .menuDetailResponseDtoList(menuDetailResponseDtoList)
                                                                                       .shopImgListResponseDtoList(shopImgListResponseDtoList)
                                                                                       .shopReviewLookupResponseDtoList(reviewResultDto.getData()
                                                                                                                                       .getShopReviewLookupResponseDto())
                                                                                       .shopArtBoardLookupResponseDtoList(artBoardResultDto.getData()
                                                                                                                                           .getShopArtBoardLookupResponseDtoList())
                                                                                       .build();

        return commonService.successResponse(SuccessCode.SHOP_DETAIL_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopDetailListResponseDto);
    }

}
