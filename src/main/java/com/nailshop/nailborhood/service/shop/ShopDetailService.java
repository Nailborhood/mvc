package com.nailshop.nailborhood.service.shop;

import com.nailshop.nailborhood.domain.address.City;
import com.nailshop.nailborhood.domain.address.Districts;
import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.*;
import com.nailshop.nailborhood.dto.shop.response.detail.*;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.address.CityRepository;
import com.nailshop.nailborhood.repository.address.DistrictsRepository;
import com.nailshop.nailborhood.repository.address.DongRepository;
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

import java.util.Collections;
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
    private final CityRepository cityRepository;
    private final DistrictsRepository districtsRepository;
    private final DongRepository dongRepository;


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
                                                                                        s.getRateAvg(),
                                                                                        s.getReviewCnt(),
                                                                                        s.getFavoriteCnt(),
                                                                                        s.getIsDeleted()
                                                                                )


                                                                        )
                                                                        .findFirst()
                                                                        .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));


        List<MenuDetailResponseDto> menuDetailResponseDtoList = menuRepository.findAllByShopId(shopId);
        List<ShopImgListResponseDto> shopImgListResponseDtoList = shopImgRepository.findAllByShopImgListByShopId(shopId);


        CommonResponseDto<Object> reviewList = shopReviewListLookupService.getAllReviewListByShopId(1, 4, "createdAt", "DESC", shopId);
        ResultDto<ShopReviewListResponseDto> reviewResultDto = ResultDto.in(reviewList.getStatus(), reviewList.getMessage());
        reviewResultDto.setData((ShopReviewListResponseDto) reviewList.getData());

        CommonResponseDto<Object> artList = shopArtBoardListService.getAllArtBoardListByShopId(1, 4, "createdAt", "DESC", shopId);
        ResultDto<ShopArtBoardListLookupResponseDto> artBoardResultDto = ResultDto.in(artList.getStatus(), artList.getMessage());
        artBoardResultDto.setData((ShopArtBoardListLookupResponseDto) artList.getData());

        // 리뷰 데이터 처리
        List<ShopAndReviewLookUpResponseDto> reviewListData;
        if (reviewResultDto.getData() != null && reviewResultDto.getData().getShopAndReviewLookUpResponseDto() != null) {
            reviewListData =  reviewResultDto.getData().getShopAndReviewLookUpResponseDto();
        } else {
            // 리뷰가 없는 경우, 적절한 처리 (예: 빈 리스트 할당)
            reviewListData = Collections.emptyList(); // 또는 적절한 메시지를 담은 객체를 생성하여 할당
        }

        // 아트보드 데이터 처리
        List<ShopArtBoardLookupResponseDto> artBoardListData;
        if (artBoardResultDto.getData() != null && artBoardResultDto.getData().getShopArtBoardLookupResponseDtoList() != null) {
            artBoardListData = artBoardResultDto.getData().getShopArtBoardLookupResponseDtoList();
        } else {
            // 아트가 없는 경우, 적절한 처리 (예: 빈 리스트 할당)
            artBoardListData = Collections.emptyList(); // 또는 적절한 메시지를 담은 객체를 생성하여 할당
        }


            ShopDetailListResponseDto shopDetailListResponseDto = ShopDetailListResponseDto.builder()
                                                                                           .shopDetailLookupResponseDto(shopDetailLookupResponseDto)
                                                                                           .menuDetailResponseDtoList(menuDetailResponseDtoList)
                                                                                           .shopImgListResponseDtoList(shopImgListResponseDtoList)
                                                                                           .shopReviewLookupResponseDtoList(reviewListData)
                                                                                           .shopArtBoardLookupResponseDtoList(artBoardListData)
                                                                                           .build();

            return commonService.successResponse(SuccessCode.SHOP_DETAIL_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopDetailListResponseDto);

        }


    // 내 매장 조회 (매장 수정 용)
    @Transactional
    public CommonResponseDto<Object> getMyShopDetail(Long shopId) {


        Shop shop = shopRepository.findByShopId(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
        ShopDetailLookupResponseDto shopDetailLookupResponseDto = Stream.of(shop)
                                                                        .map(s -> {
                                                                            String fullAddress = s.getAddress();
                                                                            int dongIndex = fullAddress.indexOf("동");
                                                                            String roadNameAddress = "";

                                                                            if (dongIndex != -1) {
                                                                                roadNameAddress = fullAddress.substring(dongIndex + 1).trim();
                                                                            }

                                                                            return new ShopDetailLookupResponseDto(
                                                                                    s.getShopId(),
                                                                                    s.getName(),
                                                                                    s.getPhone(),
                                                                                    roadNameAddress, // '도로명 주소'만 포함
                                                                                    s.getOpentime(),
                                                                                    s.getWebsite(),
                                                                                    s.getContent(),
                                                                                    s.getStatus(),
                                                                                    s.getCreatedAt(),
                                                                                    s.getRateAvg(),
                                                                                    s.getReviewCnt(),
                                                                                    s.getFavoriteCnt(),
                                                                                    s.getIsDeleted()
                                                                            );
                                                                        })
                                                                        .findFirst()
                                                                        .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        List<MenuDetailResponseDto> menuDetailResponseDtoList = menuRepository.findAllByShopId(shopId);
        List<ShopImgListResponseDto> shopImgListResponseDtoList = shopImgRepository.findAllByShopImgListByShopId(shopId);

        // 저장되어있는 주소
        Dong dong = dongRepository.findByDongId(shop.getDong()
                                                    .getDongId());
        Districts districts = districtsRepository.findByDistrictsId(dong.getDistricts()
                                                                        .getDistrictsId());
        City city = cityRepository.findByCityId(districts.getCity()
                                                         .getCityId());

        CityDto cityDto = CityDto.builder()
                                 .cityId(city.getCityId())
                                 .cityName(city.getName())
                                 .build();

        DistrictsDto districtsDto = DistrictsDto.builder()
                                                .districtsId(districts.getDistrictsId())
                                                .districtsName(districts.getName())
                                                .build();

        DongDto dongDto = DongDto.builder()
                                 .dongId(dong.getDongId())
                                 .DongName(dong.getName())
                                 .build();

        MyShopDetailListResponseDto myShopDetailListResponseDto = MyShopDetailListResponseDto.builder()
                                                                                             .shopDetailLookupResponseDto(shopDetailLookupResponseDto)
                                                                                             .menuDetailResponseDtoList(menuDetailResponseDtoList)
                                                                                             .shopImgListResponseDtoList(shopImgListResponseDtoList)
                                                                                             .cityDto(cityDto)
                                                                                             .districtsDto(districtsDto)
                                                                                             .dongDto(dongDto)
                                                                                             .build();

        return commonService.successResponse(SuccessCode.SHOP_DETAIL_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, myShopDetailListResponseDto);
    }


    // 채팅에서 사장님 매장 id 가져오기
    public Shop findMyShopId(Long ownerId) {

        return shopRepository.findAllShopByOwnerId(ownerId).orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
    }
}

