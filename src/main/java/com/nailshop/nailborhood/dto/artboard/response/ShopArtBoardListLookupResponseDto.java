package com.nailshop.nailborhood.dto.artboard.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopArtBoardListLookupResponseDto {
    private List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList;
    private PaginationDto paginationDto;

    public ShopArtBoardListLookupResponseDto(List<ShopArtBoardLookupResponseDto> shopArtBoardLookupResponseDtoList, PaginationDto paginationDto) {
        this.shopArtBoardLookupResponseDtoList = shopArtBoardLookupResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
