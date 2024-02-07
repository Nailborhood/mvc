package com.nailshop.nailborhood.dto.artboard;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArtListResponseDto {

    private List<ArtResponseDto> artResponseDtoList;
    private PaginationDto paginationDto;

    @Builder
    public ArtListResponseDto(List<ArtResponseDto> artResponseDtoList, PaginationDto paginationDto) {
        this.artResponseDtoList = artResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
