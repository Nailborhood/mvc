package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ShopReviewLookupResponseDto {
    private Long reviewId;

    private String contents;

    private Integer rate;

    private String reviewImgPath;

    private LocalDateTime createdAt;




    public ShopReviewLookupResponseDto(Long reviewId, String contents, Integer rate, String reviewImgPath, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.contents = contents;
        this.rate = rate;
        this.reviewImgPath = reviewImgPath;
        this.createdAt = createdAt;
    }
}
