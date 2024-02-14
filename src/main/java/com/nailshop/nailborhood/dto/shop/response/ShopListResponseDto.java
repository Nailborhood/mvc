package com.nailshop.nailborhood.dto.shop.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ShopListResponseDto {
    private List<ShopLookupResponseDto> shopLookupResponseDtoList;
    private PaginationDto paginationDto;

    @Builder
    public ShopListResponseDto(List<ShopLookupResponseDto> shopLookupResponseDtoList, PaginationDto paginationDto) {
        this.shopLookupResponseDtoList = shopLookupResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
