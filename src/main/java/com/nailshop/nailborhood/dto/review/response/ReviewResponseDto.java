package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {

    private Long reviewId;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private String mainImgPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReviewResponseDto(Long reviewId, String contents, Integer rate, Long likeCnt, String mainImgPath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.contents = contents;
        this.rate = rate;
        this.likeCnt = likeCnt;
        this.mainImgPath = mainImgPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
