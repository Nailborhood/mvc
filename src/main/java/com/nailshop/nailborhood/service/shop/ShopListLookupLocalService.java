package com.nailshop.nailborhood.service.shop;

import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.*;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.address.DongRepository;
import com.nailshop.nailborhood.repository.shop.MenuRepository;
import com.nailshop.nailborhood.repository.shop.ShopImgRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.artboard.ArtInquiryService;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopListLookupLocalService {
    private final CommonService commonService;
    private final DongRepository dongRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final ShopImgRepository shopImgRepository;
    private final ReviewRepository reviewRepository;
    private final ArtInquiryService artInquiryService;

    // 전체 매장 조회 (주소(동) 상관없이)
    @Transactional
    public CommonResponseDto<Object> getShopList(int page, int size, String sort, String criteria) {
        // 정렬기준 설정
        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, criteria)) : PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, criteria));
        // 페이지로 값 가져오기
        Page<Shop> shops = shopRepository.findAllNotDeleted(pageable);

        if (shops.isEmpty()) {
            throw new NotFoundException(ErrorCode.SHOP_NOT_FOUND);
        }


        // shop entity -> dto 변환
        Page<ShopLookupResponseDto> data = convertToDto(shops);

        // 매장 리스트 가져오기
        List<ShopLookupResponseDto> shopLookupResponseDtoList = data.getContent();

        // 페이지네이션 설정
        PaginationDto paginationDto = createPaginationDto(data);

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopListResponseDto shopListResponseDto = createShopListResponseDto(shopLookupResponseDtoList, paginationDto);

        return commonService.successResponse(SuccessCode.ALL_SHOP_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopListResponseDto);
    }

    // 메인 매장 조회
//
//    public CommonResponseDto<Object> getHome() {
//
//        // 좋아요 많이 받은 아트판
//        CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArt(1, 4, "likeCount","");
//        if(inquiryAllArt == null){
//            throw new NotFoundException(ErrorCode.ART_NOT_FOUND);
//        }else {
//            ResultDto<ArtListResponseDto> artListResponseDtoResultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
//            artListResponseDtoResultDto.setData((ArtListResponseDto) inquiryAllArt.getData());
//        }
//
//        // 리뷰 많은 매장
//        CommonResponseDto<Object> allReviewShopsList = getShopList(1, 4, "DESC", "reviewCnt");
//        if(allReviewShopsList == null ){
//            throw new NotFoundException(ErrorCode.SHOP_NOT_FOUND);
//        }else {
//            ResultDto<ShopListResponseDto> shopListByReviewResponseDtoResultDto = ResultDto.in(allReviewShopsList.getStatus(), allReviewShopsList.getMessage());
//            shopListByReviewResponseDtoResultDto.setData((ShopListResponseDto) allReviewShopsList.getData());
//        }
//
//        // 별점 높은 매장
//        CommonResponseDto<Object> allRateShopsList = getShopList(1, 4, "DESC", "rateAvg");
//        if(allRateShopsList == null ){
//            throw new NotFoundException(ErrorCode.SHOP_NOT_FOUND);
//        }else {
//            ResultDto<ShopListResponseDto> shopListByRateResponseDtoResultDto = ResultDto.in(allRateShopsList.getStatus(), allRateShopsList.getMessage());
//            shopListByRateResponseDtoResultDto.setData((ShopListResponseDto) allRateShopsList.getData());
//        }
//
//        return commonService.successResponse(SuccessCode.ALL_SHOP_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopListResponseDto);
//    }

    // 전체 매장 조회 (주소(동))
    @Transactional
    public CommonResponseDto<Object> getShopListByDong(String keyword, int page, int size, String sort, String criteria, Long dongId, Long districtsId, Long cityId) {


        // 정렬기준 설정
        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, criteria)) : PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, criteria));

       // dongId 유무
        Page<Shop> shops;
/*        if(keyword == null || keyword.trim()
                                     .isEmpty()) {
            if(cityId != null){
                shops = shopRepository.findAllNotDeletedByCityId(pageable,cityId);

            }else if(districtsId != null){

            }else if(dongId != null){
                shops = shopRepository.findAllNotDeletedByDongId(pageable,dongId);
            }else {
                shops = shopRepository.findAllNotDeleted(pageable);
            }
        }else {
            if (cityId != null ) {
                shops = shopRepository.findAllNotDeletedByCityIdAndKeyword(pageable, cityId, keyword);
            } else if(districtsId != null) {
                shops = shopRepository.findAllNotDeletedByDistrictsIdAndKeyword(pageable, districtsId, keyword);
            } else if(dongId != null){
                shops = shopRepository.findAllNotDeletedByDongIdAndKeyword(pageable, dongId,keyword);
            }else {
                shops = shopRepository.findALlShopListByKeyword(keyword,pageable);
            }
        }*/


        if(keyword == null || keyword.trim()
                                     .isEmpty()) {
            if(cityId != null){
                shops = shopRepository.findAllNotDeletedByCityId(pageable,cityId);
                if(districtsId != null){
                    shops = shopRepository.findAllNotDeletedByDistrictsId(pageable,districtsId);
                    if(dongId !=null){
                        shops = shopRepository.findAllNotDeletedByDongId(pageable,dongId);
                    }
                }

            }else {
                shops = shopRepository.findAllNotDeleted(pageable);
            }
        }else {
            if (cityId != null ) {
                shops = shopRepository.findAllNotDeletedByCityIdAndKeyword(pageable, cityId, keyword);
                if(districtsId != null){
                    shops = shopRepository.findAllNotDeletedByDistrictsIdAndKeyword(pageable, districtsId, keyword);
                    if(dongId !=null){
                        shops = shopRepository.findAllNotDeletedByDongIdAndKeyword(pageable, dongId,keyword);
                    }
                }
            }else {
                shops = shopRepository.findALlShopListByKeyword(keyword,pageable);
            }
        }

        if (shops.isEmpty()) {
            throw new NotFoundException(ErrorCode.SHOP_NOT_FOUND);
        }


        // shop entity -> dto 변환
        Page<ShopLookupResponseDto> data = convertToDto(shops);

        // 매장 리스트 가져오기
        List<ShopLookupResponseDto> shopLookupResponseDtoList = data.getContent();

        // 페이지네이션 설정
        PaginationDto paginationDto = createPaginationDto(data);

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopListResponseDto shopListResponseDto = createShopListResponseDto(shopLookupResponseDtoList, paginationDto);

        return commonService.successResponse(SuccessCode.ALL_SHOP_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopListResponseDto);
    }

    // entity -> dto 변환 메서드
    private Page<ShopLookupResponseDto> convertToDto(Page<Shop> shops) {
        // shop entity -> dto 변환
        return shops.map(shop -> {

            // shopImg imgNum =1 가져오기
            String shopMainImg = shopImgRepository.findByShopImgByShopIdAndShopImgId(shop.getShopId());

            // 메인에서 보여줄 값들 정해지면 뺴기
            // dto에 shop entity 값을 변환하는 과정
            ShopLookupResponseDto dto = new ShopLookupResponseDto(
                    shop.getShopId(),
                    shopMainImg,
                    shop.getName(),
                    shop.getPhone(),
                    shop.getAddress(),
                    shop.getOpentime(),
                    shop.getWebsite(),
                    shop.getContent(),
                    shop.getStatus(),
                    shop.getIsDeleted(),
                    shop.getCreatedAt(),
                    shop.getReviewCnt(),
                    shop.getFavoriteCnt(),
                    shop.getRateAvg()
            );
            // data 에 dto 반환
            return dto;
        });
    }

    // 페이지네이션
    private PaginationDto createPaginationDto(Page<ShopLookupResponseDto> data) {
        return PaginationDto.builder()
                            .totalPages(data.getTotalPages())
                            .totalElements(data.getTotalElements())
                            .pageNo(data.getNumber())
                            .isLastPage(data.isLast())
                            .build();
    }


    // 매장 정보 리스트와 페이지네이션 정보를 포함한 DTO를 생성하는 메서드
    private ShopListResponseDto createShopListResponseDto(List<ShopLookupResponseDto> shopLookupResponseDtoList, PaginationDto paginationDto) {
        return ShopListResponseDto.builder()
                                  .shopLookupResponseDtoList(shopLookupResponseDtoList)
                                  .paginationDto(paginationDto)
                                  .build();
    }


    // 조회 정렬 기준 orderby 설정
    public List<Map<String, String>> createCriteriaOptions() {
        List<Map<String, String>> sortOptions = new ArrayList<>();

        Map<String, String> option1 = new HashMap<>();
        option1.put("value", "createdAt");
        option1.put("text", "최신순");
        sortOptions.add(option1);

        Map<String, String> option2 = new HashMap<>();
        option2.put("value", "favoriteCnt");
        option2.put("text", "인기순");
        sortOptions.add(option2);

        Map<String, String> option3 = new HashMap<>();
        option3.put("value", "rateAvg");
        option3.put("text", "평점순");
        sortOptions.add(option3);

        return sortOptions;
    }



}
