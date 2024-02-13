package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReviewDetailResponseDto {

    private Long reviewId;
    private Map<Integer, String> imgPathMap;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private String reviewAuthor;
    private String reviewAuthorProfileImg;
    private LocalDateTime reviewCreatedAt;
    private LocalDateTime reviewUpdatedAt;

    @Builder
    public ReviewDetailResponseDto(Long reviewId, Map<Integer, String> imgPathMap ,String contents, Integer rate, Long likeCnt, String reviewAuthor, String reviewAuthorProfileImg, LocalDateTime reviewCreatedAt, LocalDateTime reviewUpdatedAt) {
        this.reviewId = reviewId;
        this.imgPathMap = imgPathMap;
        this.contents = contents;
        this.rate = rate;
        this.likeCnt = likeCnt;
        this.reviewAuthor = reviewAuthor;
        this.reviewAuthorProfileImg = reviewAuthorProfileImg;
        this.reviewCreatedAt = reviewCreatedAt;
        this.reviewUpdatedAt = reviewUpdatedAt;
    }
}
