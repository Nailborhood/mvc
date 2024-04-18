package com.nailshop.nailborhood.service.shop;


import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShopArtBoardListService {
    private final CommonService commonService;
    private final ArtRefRepository artRefRepository;
    private final ArtImgRepository artImgRepository;
    private final CategoryArtRepository categoryArtRepository;


    @Transactional
    public CommonResponseDto<Object> getAllArtBoardListByShopId(int page, int size, String criteria, String sort, Long shopId) {


        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, criteria)) : PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, criteria));
        // 페이지로 값 가져오기
        Page<ArtRef> artRefs = artRefRepository.findAllNotDeletedBYShopId(pageable, shopId);


        if (artRefs.isEmpty()) {
            return commonService.errorResponse(ErrorCode.ART_NOT_REGISTRATION.getDescription(), HttpStatus.OK, null);
        }

        // artRef entity -> dto 변환
        Page<ShopArtBoardLookupResponseDto> data = artRefs.map(artRef -> {
            Long artRefId = artRef.getArtRefId();
            // 아트판 이미지 가져오기
            String artImgPath = artImgRepository.findArtImgByShopIdAndArtRefId(shopId, artRefId);
            // dto에 shop entity 값을 변환하는 과정
            ShopArtBoardLookupResponseDto dto = ShopArtBoardLookupResponseDto.builder()
                    .artRefId(artRefId)
                    .name(artRef.getName())
                    .content(artRef.getContent())
                    .artImgPath(artImgPath)
                    .createdAt(artRef.getCreatedAt())
                    .build(
            );
            // data 에 dto 반환
            return dto;
        });


        // 리뷰 리스트 가져오기
        List<ShopArtBoardLookupResponseDto> artBoardLookupResponseDtoList = data.getContent();


        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(data.getTotalPages())
                                                   .totalElements(data.getTotalElements())
                                                   .pageNo(data.getNumber())
                                                   .isLastPage(data.isLast())
                                                   .build();

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopArtBoardListLookupResponseDto shopArtBoardListLookupResponseDto = ShopArtBoardListLookupResponseDto.builder()
                                                                                                               .shopArtBoardLookupResponseDtoList(artBoardLookupResponseDtoList)
                                                                                                               .paginationDto(paginationDto)
                                                                                                               .build();


        return commonService.successResponse(SuccessCode.SHOP_ART_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopArtBoardListLookupResponseDto);
    }


    @Transactional
    public CommonResponseDto<Object> getAllArtBoardListByShopIdByCategory(int page, int size, String criteria, String category, String keyword, Long shopId) {

        List<Long> categoryIdList;
        if (category != null && !category.isEmpty()){

            categoryIdList = Arrays.stream(category.split(","))
                    .map(Long::parseLong)
                    .toList();
        } else {
            categoryIdList = null;
        }

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(criteria).descending());
        // 페이지로 값 가져오기
        Page<ArtRef> artRefs;

        if (keyword == null){
            if (categoryIdList == null || categoryIdList.isEmpty()){
                // 카테고리 선택 x
                artRefs = artRefRepository.findAllNotDeletedBYShopId(pageable, shopId);
            } else {
                // 카테고리 선택 o
                int categoryIdListSize = categoryIdList.size();
                artRefs = artRefRepository.findByIsDeletedFalseAndCategory(categoryIdList, categoryIdListSize, pageable, shopId);
            }
        } else {
            if (categoryIdList == null || categoryIdList.isEmpty()) {
                // 카테고리 선택 x
                artRefs = artRefRepository.findShopArtRefListBySearch(keyword, pageable, shopId);
            } else {
                // 카테고리 선택 o
                int categoryIdListSize = categoryIdList.size();
                artRefs = artRefRepository.findShopArtByKeywordAndCategories(keyword, categoryIdList, categoryIdListSize, pageable, shopId);
            }

        }

//        if (artRefs.isEmpty()) {
//            return commonService.errorResponse(ErrorCode.ART_NOT_REGISTRATION.getDescription(), HttpStatus.OK, null);
//        }

        // artRef entity -> dto 변환
        Page<ShopArtBoardLookupResponseDto> data = artRefs.map(artRef -> {
            Long artRefId = artRef.getArtRefId();
            // 아트판 이미지 가져오기
            String artImgPath = artImgRepository.findArtImgByShopIdAndArtRefId(shopId, artRefId);
            List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRef.getArtRefId());
            // dto에 shop entity 값을 변환하는 과정
            ShopArtBoardLookupResponseDto dto = new ShopArtBoardLookupResponseDto(
                    artRefId,
                    shopId,
                    artRef.getName(),
                    artRef.getContent(),
                    artImgPath,
                    artRef.getCreatedAt(),
                    categoryIdList,
                    categoryTypeList
            );
            // data 에 dto 반환
            return dto;
        });


        // 리뷰 리스트 가져오기
        List<ShopArtBoardLookupResponseDto> artBoardLookupResponseDtoList = data.getContent();


        // 페이지네이션 설정
        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(data.getTotalPages())
                .totalElements(data.getTotalElements())
                .pageNo(data.getNumber())
                .isLastPage(data.isLast())
                .build();

        // 페이지네이션을 포함한 매장 리스트 반환
        ShopArtBoardListLookupResponseDto shopArtBoardListLookupResponseDto = ShopArtBoardListLookupResponseDto.builder()
                .shopArtBoardLookupResponseDtoList(artBoardLookupResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.SHOP_ART_LOOKUP_SUCCESS.getDescription(), HttpStatus.OK, shopArtBoardListLookupResponseDto);
    }

    public List<Map<String, String>> createCriteriaOptions() {
        List<Map<String, String>> sortOptions = new ArrayList<>();

        Map<String, String> option1 = new HashMap<>();
        option1.put("value", "likeCount");
        option1.put("text", "인기순");

        sortOptions.add(option1);

        Map<String, String> option2 = new HashMap<>();
        option2.put("value", "createdAt");
        option2.put("text", "최신순");
        sortOptions.add(option2);

        return sortOptions;
    }
}
