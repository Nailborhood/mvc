package com.nailshop.nailborhood.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {

    private Long reviewId;
    private String mainImgPath;
    private List<String> categoryTypeList;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReviewResponseDto(Long reviewId, String mainImgPath, List<String> categoryTypeList, String contents, Integer rate, Long likeCnt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.mainImgPath = mainImgPath;
        this.categoryTypeList = categoryTypeList;
        this.contents = contents;
        this.rate = rate;
        this.likeCnt = likeCnt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
