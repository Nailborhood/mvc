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
    private Long shopId;
    private String mainImgPath;
    private List<Long> categoryIdList;
    private List<String> categoryTypeList;
    private String shopName;
    private String shopAddress;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReviewResponseDto(Long reviewId, Long shopId, String mainImgPath, List<Long> categoryIdList, List<String> categoryTypeList, String shopName,String shopAddress, String contents, Integer rate, Long likeCnt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.shopId = shopId;
        this.mainImgPath = mainImgPath;
        this.categoryIdList = categoryIdList;
        this.categoryTypeList = categoryTypeList;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.contents = contents;
        this.rate = rate;
        this.likeCnt = likeCnt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
