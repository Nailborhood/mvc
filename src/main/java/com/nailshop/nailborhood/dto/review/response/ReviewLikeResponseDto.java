package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewLikeResponseDto {

    private Boolean reviewLikeStatus;

    @Builder
    public ReviewLikeResponseDto(Boolean reviewLikeStatus) {
        this.reviewLikeStatus = reviewLikeStatus;
    }
}
