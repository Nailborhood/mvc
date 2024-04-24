package com.nailshop.nailborhood.dto.shop.response.detail;

import com.nailshop.nailborhood.dto.artboard.response.ShopArtBoardLookupResponseDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewLookupResponseDto;
import com.nailshop.nailborhood.dto.shop.response.DongDto;
import com.nailshop.nailborhood.dto.shop.response.ShopAndReviewLookUpResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class ShopDetailListResponseDto {
    private ShopDetailLookupResponseDto shopDetailLookupResponseDto;
    private List<ShopImgListResponseDto> shopImgListResponseDtoList;
    private List<MenuDetailResponseDto> menuDetailResponseDtoList;
    private List<ShopAndReviewLookUpResponseDto> shopReviewLookupResponseDtoList; // 리뷰리스트
    private List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList; // 아트 리스트
    private ShopCertificateImgResponseDto shopCertificateImgResponseDto;

}
