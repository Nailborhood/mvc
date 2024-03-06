package com.nailshop.nailborhood.dto.shop.response.admin;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AllShopsListResponseDto {
    private List<AllShopsLookupResponseDto> allShopsLookupResponseDtoList;
    private PaginationDto paginationDto;

    @Builder
    public AllShopsListResponseDto(List<AllShopsLookupResponseDto> allShopsLookupResponseDtoList, PaginationDto paginationDto) {
        this.allShopsLookupResponseDtoList = allShopsLookupResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
