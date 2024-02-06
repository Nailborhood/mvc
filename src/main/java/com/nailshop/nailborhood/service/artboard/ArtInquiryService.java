package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.dto.artboard.ArtDetailResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArtInquiryService {

    private final CommonService commonService;
    private final ArtRefRepository artRefRepository;
    private final ArtImgRepository artImgRepository;
    private final CategoryArtRepository categoryArtRepository;

    // 전체 조회
    public CommonResponseDto<Object> inquiryAllArt(int page, int size, String sortBy) {

        // Page 설정 및 ArtRefList get
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy).descending());

        Page<ArtRef> artRefPage = artRefRepository.findByIsDeletedFalse(pageable);
        if (artRefPage.isEmpty()) throw new NotFoundException(ErrorCode.ART_NOT_FOUND);

        List<ArtRef> artRefList = artRefPage.getContent();
        List<ArtResponseDto> artResponseDtoList = new ArrayList<>();

        // ArtResponseDto build
        for (ArtRef artRef : artRefList){

            String mainImgPath = artRef.getArtImgList().getFirst().getImgPath();
            String shopName = artRef.getShop().getName();

            List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRef.getArtRefId());

            ArtResponseDto artResponseDto = ArtResponseDto.builder()
                    .name(artRef.getName())
                    .content(artRef.getContent())
                    .likeCount(artRef.getLikeCount())
                    .mainImgPath(mainImgPath)
                    .shopName(shopName)
                    .categoryTypeList(categoryTypeList)
                    .createdAt(artRef.getCreatedAt())
                    .updatedAt(artRef.getUpdatedAt())
                    .build();

            artResponseDtoList.add(artResponseDto);
        }

        // Pagination 설정
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
    public CommonResponseDto<Object> inquiryArt(Long artRefId) {

        // ArtRef get
        ArtRef artRef = artRefRepository.findById(artRefId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // ArtImg get
        List<ArtImg> artImgList = artImgRepository.findByArtRefId(artRefId);
        Map<Integer, String> artImgPathMap = new HashMap<>();
        for (ArtImg artImg : artImgList){
            artImgPathMap.put(artImg.getImgNum(), artImg.getImgPath());
        }

        // ArtDetailResponseDto build
        String shopName = artRef.getShop().getName();
        List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRefId);

        ArtDetailResponseDto artDetailResponseDto = ArtDetailResponseDto.builder()
                .name(artRef.getName())
                .content(artRef.getContent())
                .likeCount(artRef.getLikeCount())
                .shopName(shopName)
                .categoryTypeList(categoryTypeList)
                .imgPathMap(artImgPathMap)
                .createdAt(artRef.getCreatedAt())
                .updatedAt(artRef.getUpdatedAt())
                .build();

        return commonService.successResponse(SuccessCode.ART_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, artDetailResponseDto);
    }
}
