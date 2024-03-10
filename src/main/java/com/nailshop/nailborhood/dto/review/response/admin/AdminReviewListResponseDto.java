package com.nailshop.nailborhood.dto.review.response.admin;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminReviewListResponseDto {

    private List<AdminReviewResponseDto> adminReviewResponseDtoList;
    private PaginationDto paginationDto;

    @Builder
    public AdminReviewListResponseDto(List<AdminReviewResponseDto> adminReviewResponseDtoList, PaginationDto paginationDto) {
        this.adminReviewResponseDtoList = adminReviewResponseDtoList;
        this.paginationDto = paginationDto;
    }
}
