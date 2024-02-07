package com.nailshop.nailborhood.dto.artBoard.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopArtBoardListLookupResponseDto {
    private List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList;

    public ShopArtBoardListLookupResponseDto(List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList) {
        this.shopArtBoardLookupResponseDtoList = shopArtBoardLookupResponseDtoList;
    }
}
