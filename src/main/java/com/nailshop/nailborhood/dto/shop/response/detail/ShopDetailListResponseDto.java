package com.nailshop.nailborhood.dto.shop.response.detail;

import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewLookupResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ShopDetailListResponseDto {
    private ShopDetailLookupResponseDto shopDetailLookupResponseDto;
    private List<ShopImgListResponseDto> shopImgListResponseDtoList;
    private List<MenuDetailResponseDto> menuDetailResponseDtoList;
    private List<ShopReviewLookupResponseDto> shopReviewLookupResponseDtoList; // 리뷰리스트
    private List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList; // 아트 리스트

    @Builder
    public ShopDetailListResponseDto(ShopDetailLookupResponseDto shopDetailLookupResponseDto, List<ShopImgListResponseDto> shopImgListResponseDtoList, List<MenuDetailResponseDto> menuDetailResponseDtoList, List<ShopReviewLookupResponseDto> shopReviewLookupResponseDtoList, List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList) {
        this.shopDetailLookupResponseDto = shopDetailLookupResponseDto;
        this.shopImgListResponseDtoList = shopImgListResponseDtoList;
        this.menuDetailResponseDtoList = menuDetailResponseDtoList;
        this.shopReviewLookupResponseDtoList = shopReviewLookupResponseDtoList;
        this.shopArtBoardLookupResponseDtoList = shopArtBoardLookupResponseDtoList;
    }
}
