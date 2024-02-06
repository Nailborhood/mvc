package com.nailshop.nailborhood.dto.shop.response;

import com.nailshop.nailborhood.dto.artBoard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewLookupResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class ShopDetailListResponseDto {
    private ShopDetailLookupResponseDto shopDetailLookupResponseDto;
    private List<ShopImgListResponseDto> shopImgListResponseDto;
    private List<MenuDetailResponseDto> menuDetailResponseDto;
    //TODO: 리뷰 리스트 , 아트 리스트
    private List<ShopReviewLookupResponseDto> shopReviewLookupResponseDtoList;
    private List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList;

    public ShopDetailListResponseDto(ShopDetailLookupResponseDto shopDetailLookupResponseDto, List<ShopImgListResponseDto> shopImgListResponseDto, List<MenuDetailResponseDto> menuDetailResponseDto, List<ShopReviewLookupResponseDto> shopReviewLookupResponseDtoList, List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList) {
        this.shopDetailLookupResponseDto = shopDetailLookupResponseDto;
        this.shopImgListResponseDto = shopImgListResponseDto;
        this.menuDetailResponseDto = menuDetailResponseDto;
        this.shopReviewLookupResponseDtoList = shopReviewLookupResponseDtoList;
        this.shopArtBoardLookupResponseDtoList = shopArtBoardLookupResponseDtoList;
    }
}
