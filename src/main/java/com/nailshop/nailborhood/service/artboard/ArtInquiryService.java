package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.artboard.ArtDetailResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtBookMarkRepository;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ArtInquiryService {

    private final CommonService commonService;
    private final ArtRefRepository artRefRepository;
    private final ArtImgRepository artImgRepository;
    private final ArtLikeRepository artLikeRepository;
    private final MemberRepository memberRepository;
    private final CategoryArtRepository categoryArtRepository;
    private final ArtBookMarkRepository artBookMarkRepository;

    // 전체 조회
    public CommonResponseDto<Object> inquiryAllArt(int page, int size, String sortBy, String category, String keyword) {

        // category 리스트화
        List<Long> categoryIdList = null;
        if (category != null && !category.isEmpty()) {

            categoryIdList = Arrays.stream(category.split(","))
                                   .map(Long::parseLong)
                                   .toList();
        }

        // Page 설정 및 ArtRefList get
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());

        Page<ArtRef> artRefPage;
        List<ArtRef> artRefList;

        // keyword 가 null
        if (keyword == null) {
            if (categoryIdList == null || categoryIdList.isEmpty()) {
                // 카테고리 선택 x
                artRefPage = artRefRepository.findByIsDeletedFalse(pageable);
            } else {
                // 카테고리 선택 o
                int categoryIdListSize = categoryIdList.size();
                artRefPage = artRefRepository.findByIsDeletedFalseAndCategoryIdListIn(categoryIdList, categoryIdListSize, pageable);
            }
        } else {
            if (categoryIdList == null || categoryIdList.isEmpty()) {
                // 카테고리 선택 x
                artRefPage = artRefRepository.findArtRefListBySearch(keyword, pageable);
            } else {
                // 카테고리 선택 o
                int categoryIdListSize = categoryIdList.size();
                artRefPage = artRefRepository.findByKeywordAndCategories(keyword, categoryIdList, categoryIdListSize, pageable);
            }

        }
        artRefList = artRefPage.getContent();

        List<ArtResponseDto> artResponseDtoList = new ArrayList<>();

        // ArtResponseDto build
        for (ArtRef artRef : artRefList) {

            String mainImgPath = artRef.getArtImgList()
                                       .getFirst()
                                       .getImgPath();
            String shopName = artRef.getShop()
                                    .getName();

            List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRef.getArtRefId());

            ArtResponseDto artResponseDto = ArtResponseDto.builder()
                                                          .id(artRef.getArtRefId())
                                                          .name(artRef.getName())
                                                          .content(artRef.getContent())
                                                          .likeCount(artRef.getLikeCount())
                                                          .mainImgPath(mainImgPath)
                                                          .shopName(shopName)
                                                          .categoryIdList(categoryIdList)
                                                          .categoryTypeList(categoryTypeList)
                                                          .createdAt(artRef.getCreatedAt())
                                                          .updatedAt(artRef.getUpdatedAt())
                                                          .bookMarkCount(artRef.getBookMarkCount())
                                                          .build();

            artResponseDtoList.add(artResponseDto);
        }

        // PaginationDto build
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(artRefPage.getTotalPages())
                                                   .totalElements(artRefPage.getTotalElements())
                                                   .pageNo(artRefPage.getNumber())
                                                   .isLastPage(artRefPage.isLast())
                                                   .build();

        // ArtListResponseDto build
        ArtListResponseDto artListResponseDto = ArtListResponseDto.builder()
                                                                  .artResponseDtoList(artResponseDtoList)
                                                                  .paginationDto(paginationDto)
                                                                  .build();

        return commonService.successResponse(SuccessCode.ART_ALL_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, artListResponseDto);
    }

    // 상세 조회
    public CommonResponseDto<Object> inquiryArt(Long artRefId, Long memberId) {

        // ArtRef get
        ArtRef artRef = artRefRepository.findById(artRefId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // ArtImg get
        List<ArtImg> artImgList = artImgRepository.findByArtRefId(artRefId);
        Map<Integer, String> artImgPathMap = new HashMap<>();
        for (ArtImg artImg : artImgList) {
            artImgPathMap.put(artImg.getImgNum(), artImg.getImgPath());
        }

        Boolean artLikeStatus = artLikeRepository.findStatusByMemberIdAnAndArtRefId(memberId, artRefId);
        if (artLikeStatus == null) {
            artLikeStatus = false;
        }

        // 북마크
        Boolean artBookMarkStatus = artBookMarkRepository.findStatusByMemberIdAnAndArtRefId(memberId, artRefId);
        if (artBookMarkStatus == null) {
            artBookMarkStatus = false;
        }

        // ArtDetailResponseDto build
        String shopName = artRef.getShop()
                                .getName();
        List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRefId);

        ArtDetailResponseDto artDetailResponseDto = ArtDetailResponseDto.builder()
                                                                        .shopId(artRef.getShop()
                                                                                      .getShopId())
                                                                        .artRefId(artRef.getArtRefId())
                                                                        .name(artRef.getName())
                                                                        .content(artRef.getContent())
                                                                        .likeCount(artRef.getLikeCount())
                                                                        .shopName(shopName)
                                                                        .categoryTypeList(categoryTypeList)
                                                                        .imgPathMap(artImgPathMap)
                                                                        .createdAt(artRef.getCreatedAt())
                                                                        .updatedAt(artRef.getUpdatedAt())
                                                                        .artLikeStatus(artLikeStatus)
                                                                        .bookMarkCount(artRef.getBookMarkCount())
                                                                        .artBookMarkStatus(artBookMarkStatus)
                                                                        .shopAddress(artRef.getShop()
                                                                                           .getAddress())
                                                                        .build();

        return commonService.successResponse(SuccessCode.ART_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, artDetailResponseDto);
    }

    public CommonResponseDto<Object> inquiryArtForGuest(Long artRefId) {

        // ArtRef get
        ArtRef artRef = artRefRepository.findById(artRefId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // ArtImg get
        List<ArtImg> artImgList = artImgRepository.findByArtRefId(artRefId);
        Map<Integer, String> artImgPathMap = new HashMap<>();
        for (ArtImg artImg : artImgList) {
            artImgPathMap.put(artImg.getImgNum(), artImg.getImgPath());
        }

        // ArtDetailResponseDto build
        String shopName = artRef.getShop()
                                .getName();
        List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRefId);

        ArtDetailResponseDto artDetailResponseDto = ArtDetailResponseDto.builder()
                                                                        .shopId(artRef.getShop()
                                                                                      .getShopId())
                                                                        .artRefId(artRef.getArtRefId())
                                                                        .name(artRef.getName())
                                                                        .content(artRef.getContent())
                                                                        .likeCount(artRef.getLikeCount())
                                                                        .shopName(shopName)
                                                                        .categoryTypeList(categoryTypeList)
                                                                        .imgPathMap(artImgPathMap)
                                                                        .createdAt(artRef.getCreatedAt())
                                                                        .updatedAt(artRef.getUpdatedAt())
                                                                        .bookMarkCount(artRef.getBookMarkCount())
                                                                        .shopAddress(artRef.getShop()
                                                                                           .getAddress())
                                                                        .build();

        return commonService.successResponse(SuccessCode.ART_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, artDetailResponseDto);
    }

    // shopId로 조회
    public CommonResponseDto<Object> inquiryAllArtByShopId(MemberDetails memberDetails, int page, int size, String sortBy, String keyword) {

        // member, owner, shop get
        Member member = memberDetails.getMember();
        Owner owner = member.getOwner();
        Shop shop = owner.getShop();

        // Page 설정 및 ArtRefList get
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                                                                  .descending());

        Page<ArtRef> artRefPage;

        if (keyword == null) {
            artRefPage = artRefRepository.findAllNotDeletedBYShopId(pageable, shop.getShopId());
        } else {
            artRefPage = artRefRepository.findArtRefByKeywordAndShopId(pageable, keyword, shop.getShopId());
        }

        List<ArtRef> artRefList = artRefPage.getContent();
        List<ArtResponseDto> artResponseDtoList = new ArrayList<>();

        // ArtResponseDto build
        for (ArtRef artRef : artRefList) {

            String mainImgPath = artRef.getArtImgList()
                                       .getFirst()
                                       .getImgPath();
            String shopName = artRef.getShop()
                                    .getName();

            List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRef.getArtRefId());

            ArtResponseDto artResponseDto = ArtResponseDto.builder()
                                                          .id(artRef.getArtRefId())
                                                          .name(artRef.getName())
                                                          .content(artRef.getContent())
                                                          .likeCount(artRef.getLikeCount())
                                                          .mainImgPath(mainImgPath)
                                                          .shopName(shopName)
                                                          .categoryTypeList(categoryTypeList)
                                                          .createdAt(artRef.getCreatedAt())
                                                          .updatedAt(artRef.getUpdatedAt())
                                                          .bookMarkCount(artRef.getBookMarkCount())
                                                          .build();

            artResponseDtoList.add(artResponseDto);
        }

        // PaginationDto build
        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(artRefPage.getTotalPages())
                                                   .totalElements(artRefPage.getTotalElements())
                                                   .pageNo(artRefPage.getNumber())
                                                   .isLastPage(artRefPage.isLast())
                                                   .build();

        // ArtListResponseDto build
        ArtListResponseDto artListResponseDto = ArtListResponseDto.builder()
                                                                  .artResponseDtoList(artResponseDtoList)
                                                                  .paginationDto(paginationDto)
                                                                  .build();

        return commonService.successResponse(SuccessCode.ART_ALL_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, artListResponseDto);
    }

    public List<Map<String, String>> createCriteriaOptions() {
        List<Map<String, String>> sortOptions = new ArrayList<>();

        Map<String, String> option1 = new HashMap<>();
        option1.put("value", "likeCount");
        option1.put("text", "인기순");

        sortOptions.add(option1);

        Map<String, String> option2 = new HashMap<>();
        option2.put("value", "updatedAt");
        option2.put("text", "최신순");
        sortOptions.add(option2);

        Map<String, String> option3 = new HashMap<>();
        option3.put("value", "bookMarkCount");
        option3.put("text", "저장순");
        sortOptions.add(option3);

        return sortOptions;
    }
}
