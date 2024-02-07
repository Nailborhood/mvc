package com.nailshop.nailborhood.dto.review.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopReviewListLookupResponseDto {
    private List<ShopReviewLookupResponseDto> shopReviewLookupResponseDto;
    private PaginationDto paginationDto;


    public ShopReviewListLookupResponseDto(List<ShopReviewLookupResponseDto> shopReviewLookupResponseDto, PaginationDto paginationDto) {
        this.shopReviewLookupResponseDto = shopReviewLookupResponseDto;
        this.paginationDto = paginationDto;
    }
}
