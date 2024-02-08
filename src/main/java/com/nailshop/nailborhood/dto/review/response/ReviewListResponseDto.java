package com.nailshop.nailborhood.dto.review.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewListResponseDto {

    private List<ReviewResponseDto> reviewResponseDtoList;
    private PaginationDto paginationDto;

    @Builder
    public ReviewListResponseDto(List<ReviewResponseDto> reviewResponseDtoList, PaginationDto paginationDto) {
        this.reviewResponseDtoList = reviewResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
