package com.nailshop.nailborhood.dto.shop.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopReviewListResponseDto {
    private List<ShopAndReviewLookUpResponseDto> shopAndReviewLookUpResponseDto;
    private PaginationDto paginationDto;

    public ShopReviewListResponseDto(List<ShopAndReviewLookUpResponseDto> shopAndReviewLookUpResponseDto, PaginationDto paginationDto) {
        this.shopAndReviewLookUpResponseDto = shopAndReviewLookUpResponseDto;
        this.paginationDto = paginationDto;
    }
}
