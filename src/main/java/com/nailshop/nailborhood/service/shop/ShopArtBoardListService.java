package com.nailshop.nailborhood.service.shop;


import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardListLookupResponseDto;
import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopArtBoardListService {
    private final CommonService commonService;
    private final ArtRefRepository artRefRepository;
    private final ArtImgRepository artImgRepository;


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
            ShopArtBoardLookupResponseDto dto = new ShopArtBoardLookupResponseDto(
                    artRefId,
                    artRef.getName(),
                    artRef.getContent(),
                    artImgPath,
                    artRef.getCreatedAt()
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

}
