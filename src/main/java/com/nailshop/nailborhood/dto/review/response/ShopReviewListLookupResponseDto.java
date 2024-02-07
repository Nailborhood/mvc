package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopReviewListLookupResponseDto {
    private List<ShopReviewLookupResponseDto> shopReviewLookupResponseDto;


    public ShopReviewListLookupResponseDto(List<ShopReviewLookupResponseDto> shopReviewLookupResponseDto) {
        this.shopReviewLookupResponseDto = shopReviewLookupResponseDto;
    }
}
