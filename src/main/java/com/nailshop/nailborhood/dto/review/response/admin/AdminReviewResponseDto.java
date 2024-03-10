package com.nailshop.nailborhood.dto.review.response.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class AdminReviewResponseDto {

    private Long reviewId;
    private String mainImgPath;
    private List<String> categoryTypeList;
    private String shopName;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String reviewer;
    private boolean isDeleted;

    @Builder
    public AdminReviewResponseDto(Long reviewId, String mainImgPath, List<String> categoryTypeList, String shopName, String contents, Integer rate, Long likeCnt, LocalDateTime createdAt, LocalDateTime updatedAt, String reviewer, boolean isDeleted) {
        this.reviewId = reviewId;
        this.mainImgPath = mainImgPath;
        this.categoryTypeList = categoryTypeList;
        this.shopName = shopName;
        this.contents = contents;
        this.rate = rate;
        this.likeCnt = likeCnt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewer = reviewer;
        this.isDeleted = isDeleted;
    }
}
