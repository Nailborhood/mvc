package com.nailshop.nailborhood.dto.mypage;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyReviewListResponseDto {

    private List<ReviewResponseDto> reviewResponseDtoList;
    private PaginationDto paginationDto;


    @Builder
    public MyReviewListResponseDto(List<ReviewResponseDto> reviewResponseDtoList, PaginationDto paginationDto) {
        this.reviewResponseDtoList = reviewResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
