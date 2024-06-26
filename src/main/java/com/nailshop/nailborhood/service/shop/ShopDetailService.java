package com.nailshop.nailborhood.service.shop;

import com.nailshop.nailborhood.domain.address.City;
import com.nailshop.nailborhood.domain.address.Districts;
import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.shop.CertificateImg;
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
import com.nailshop.nailborhood.repository.member.FavoriteRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.CertificateImgRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.member.MemberService;
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
    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberService memberService;
    private final CertificateImgRepository certificateImgRepository;

    // 매장 상세 조회
    @Transactional
    public CommonResponseDto<Object> getShopDetail(Long shopId, Long memberId) {

        Member member = memberService.getMemberInfo(memberId);
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));


        ShopDetailLookupResponseDto shopDetailLookupResponseDto = ShopDetailLookupResponseDto.builder()
                                                                                             .name(shop.getName())
                                                                                             .phone(shop.getPhone())
                                                                                             .isDeleted(shop.getIsDeleted())
                                                                                             .shopId(shop.getShopId())
                                                                                             .createdAt(shop.getCreatedAt())
                                                                                             .address(shop.getAddress())
                                                                                             .rateAvg(shop.getRateAvg())
                                                                                             .reviewCnt(shop.getReviewCnt())
                                                                                             .website(shop.getWebsite())
                                                                                             .status(shop.getStatus())
                                                                                             .content(shop.getContent())
                                                                                             .favoriteCnt(shop.getFavoriteCnt())
                                                                                             .opentime(shop.getOpentime())
                                                                                             .build();


        if (member != null) {
            Boolean heartStatus = favoriteRepository.findStatusByMemberIdAndShopId(memberId, shopId);
            if (heartStatus == null) {
                heartStatus = false;
            }
            shopDetailLookupResponseDto.setHeartStatus(heartStatus);
        }
//        }else {
//            Boolean heartStatus = false;
//        }


        List<MenuDetailResponseDto> menuDetailResponseDtoList = menuRepository.findAllByShopId(shopId);
        List<ShopImgListResponseDto> shopImgListResponseDtoList = shopImgRepository.findAllByShopImgListByShopId(shopId);
        CertificateImg certificateImg = certificateImgRepository.findByShopId(shopId);

        ShopCertificateImgResponseDto shopCertificateImgResponseDto = ShopCertificateImgResponseDto.builder()
                                                                                                   .imgNum(certificateImg.getImgNum())
                                                                                                   .imgPath(certificateImg.getImgPath())
                                                                                                   .build();

        CommonResponseDto<Object> reviewList = shopReviewListLookupService.getAllReviewListByShopId(1, 4, "createdAt", "DESC", shopId);
//        if(reviewList == null){
//            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
//        }
        ResultDto<ShopReviewListResponseDto> reviewResultDto = ResultDto.in(reviewList.getStatus(), reviewList.getMessage());
        reviewResultDto.setData((ShopReviewListResponseDto) reviewList.getData());

        CommonResponseDto<Object> artList = shopArtBoardListService.getAllArtBoardListByShopId(1, 4, "createdAt", "DESC", shopId);
//        if(artList == null){
//            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
//        }
        ResultDto<ShopArtBoardListLookupResponseDto> artBoardResultDto = ResultDto.in(artList.getStatus(), artList.getMessage());
        artBoardResultDto.setData((ShopArtBoardListLookupResponseDto) artList.getData());

        // 리뷰 데이터 처리
        List<ShopAndReviewLookUpResponseDto> reviewListData;
        if (reviewResultDto.getData() != null && reviewResultDto.getData()
                                                                .getShopAndReviewLookUpResponseDto() != null) {
            reviewListData = reviewResultDto.getData()
                                            .getShopAndReviewLookUpResponseDto();
        } else {
            // 리뷰가 없는 경우, 적절한 처리 (예: 빈 리스트 할당)
            reviewListData = Collections.emptyList(); // 또는 적절한 메시지를 담은 객체를 생성하여 할당
//            if (reviewListData.isEmpty()) throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }

        // 아트보드 데이터 처리
        List<ShopArtBoardLookupResponseDto> artBoardListData;
        if (artBoardResultDto.getData() != null && artBoardResultDto.getData()
                                                                    .getShopArtBoardLookupResponseDtoList() != null) {
            artBoardListData = artBoardResultDto.getData()
                                                .getShopArtBoardLookupResponseDtoList();
        } else {
            // 아트가 없는 경우, 적절한 처리 (예: 빈 리스트 할당)
            artBoardListData = Collections.emptyList(); // 또는 적절한 메시지를 담은 객체를 생성하여 할당
//            if (artBoardListData.isEmpty()) throw new NotFoundException(ErrorCode.ART_NOT_FOUND);
        }


        ShopDetailListResponseDto shopDetailListResponseDto = ShopDetailListResponseDto.builder()
                                                                                       .shopDetailLookupResponseDto(shopDetailLookupResponseDto)
                                                                                       .menuDetailResponseDtoList(menuDetailResponseDtoList)
                                                                                       .shopImgListResponseDtoList(shopImgListResponseDtoList)
                                                                                       .shopReviewLookupResponseDtoList(reviewListData)
                                                                                       .shopArtBoardLookupResponseDtoList(artBoardListData)
                                                                                       .shopCertificateImgResponseDto(shopCertificateImgResponseDto)
                                                                                       .build();

        return commonService.successResponse(SuccessCode.SHOP_DETAIL_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopDetailListResponseDto);

    }


    // 내 매장 조회 (매장 수정 용)
    @Transactional
    public CommonResponseDto<Object> getMyShopDetail(Long memberId) {

        // member, owner, shop get
        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Owner owner = ownerRepository.findByOwnerId(member.getOwner()
                                                          .getOwnerId())
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));


        Shop shop = owner.getShop();

        Shop myShop = shopRepository.findByShopId(shop.getShopId())
                                    .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        ShopDetailLookupResponseDto shopDetailLookupResponseDto = Stream.of(myShop)
                                                                        .map(s -> {
                                                                            String fullAddress = s.getAddress();
                                                                            int dongIndex = fullAddress.indexOf("동");
                                                                            String roadNameAddress = "";

                                                                            if (dongIndex != -1) {
                                                                                roadNameAddress = fullAddress.substring(dongIndex + 1)
                                                                                                             .trim();
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

        List<MenuDetailResponseDto> menuDetailResponseDtoList = menuRepository.findAllByShopId(myShop.getShopId());
        List<ShopImgListResponseDto> shopImgListResponseDtoList = shopImgRepository.findAllByShopImgListByShopId(myShop.getShopId());

        // 저장되어있는 주소
        Dong dong = dongRepository.findByDongId(myShop.getDong()
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
                                 .dongName(dong.getName())
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


        return shopRepository.findAllShopByOwnerId(ownerId)
                             .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
    }

    // 매장 상세 조회 (관리자 채팅 상세)
    @Transactional
    public CommonResponseDto<Object> getShopDetailByAdmin(Long shopId) {


        Shop shop = shopRepository.findByShopId(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));
        ShopDetailLookupResponseDto shopDetailLookupResponseDto = Stream.of(shop)
                                                                        .map(s -> {
                                                                            String fullAddress = s.getAddress();
                                                                            int dongIndex = fullAddress.indexOf("동");
                                                                            String roadNameAddress = "";

                                                                            if (dongIndex != -1) {
                                                                                roadNameAddress = fullAddress.substring(dongIndex + 1)
                                                                                                             .trim();
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
                                 .dongName(dong.getName())
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


    public CommonResponseDto<Object> getShopDetailForGuest(Long shopId) {
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));


        ShopDetailLookupResponseDto shopDetailLookupResponseDto = ShopDetailLookupResponseDto.builder()
                                                                                             .name(shop.getName())
                                                                                             .phone(shop.getPhone())
                                                                                             .isDeleted(shop.getIsDeleted())
                                                                                             .shopId(shop.getShopId())
                                                                                             .createdAt(shop.getCreatedAt())
                                                                                             .address(shop.getAddress())
                                                                                             .rateAvg(shop.getRateAvg())
                                                                                             .reviewCnt(shop.getReviewCnt())
                                                                                             .website(shop.getWebsite())
                                                                                             .status(shop.getStatus())
                                                                                             .content(shop.getContent())
                                                                                             .favoriteCnt(shop.getFavoriteCnt())
                                                                                             .opentime(shop.getOpentime())
                                                                                             .build();


        List<MenuDetailResponseDto> menuDetailResponseDtoList = menuRepository.findAllByShopId(shopId);
        List<ShopImgListResponseDto> shopImgListResponseDtoList = shopImgRepository.findAllByShopImgListByShopId(shopId);


        CommonResponseDto<Object> reviewList = shopReviewListLookupService.getAllReviewListByShopId(1, 4, "createdAt", "DESC", shopId);
//        if(reviewList == null){
//            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
//        }
        ResultDto<ShopReviewListResponseDto> reviewResultDto = ResultDto.in(reviewList.getStatus(), reviewList.getMessage());
        reviewResultDto.setData((ShopReviewListResponseDto) reviewList.getData());


        CommonResponseDto<Object> artList = shopArtBoardListService.getAllArtBoardListByShopId(1, 4, "createdAt", "DESC", shopId);
//        if(artList == null){
//            throw new NotFoundException(ErrorCode.ART_NOT_FOUND);
//        }
        ResultDto<ShopArtBoardListLookupResponseDto> artBoardResultDto = ResultDto.in(artList.getStatus(), artList.getMessage());
        artBoardResultDto.setData((ShopArtBoardListLookupResponseDto) artList.getData());

        // 리뷰 데이터 처리
        List<ShopAndReviewLookUpResponseDto> reviewListData;
        if (reviewResultDto.getData() != null && reviewResultDto.getData()
                                                                .getShopAndReviewLookUpResponseDto() != null) {
            reviewListData = reviewResultDto.getData()
                                            .getShopAndReviewLookUpResponseDto();
        } else {
            // 리뷰가 없는 경우, 적절한 처리 (예: 빈 리스트 할당)
            reviewListData = Collections.emptyList(); // 또는 적절한 메시지를 담은 객체를 생성하여 할당
//            reviewListData = reviewResultDto.getMessage();
//            if (reviewListData.isEmpty()) throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }

        // 아트보드 데이터 처리
        List<ShopArtBoardLookupResponseDto> artBoardListData;
        if (artBoardResultDto.getData() != null && artBoardResultDto.getData()
                                                                    .getShopArtBoardLookupResponseDtoList() != null) {
            artBoardListData = artBoardResultDto.getData()
                                                .getShopArtBoardLookupResponseDtoList();
        } else {
            // 아트가 없는 경우, 적절한 처리 (예: 빈 리스트 할당)
            artBoardListData = Collections.emptyList(); // 또는 적절한 메시지를 담은 객체를 생성하여 할당
//            if (artBoardListData.isEmpty()) throw new NotFoundException(ErrorCode.ART_NOT_FOUND);
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
}

