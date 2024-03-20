package com.nailshop.nailborhood.dto.review.response;

import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.ShopStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReviewDetailResponseDto {

    private Long reviewId;
    private String shopName;
    private ShopStatus shopStatus;
    private String shopAddress;
    private String reviewReportStatus;
    private List<String> categoryTypeList;
    private Map<Integer, String> imgPathMap;
    private String contents;
    private Integer rate;
    private Long likeCnt;
    private String reviewAuthor;
    private String reviewAuthorProfileImg;
    private LocalDateTime reviewCreatedAt;
    private LocalDateTime reviewUpdatedAt;

    @Builder
    public ReviewDetailResponseDto(Long reviewId, String shopName, ShopStatus shopStatus, String  shopAddress, String reviewReportStatus, List<String> categoryTypeList, Map<Integer, String> imgPathMap, String contents, Integer rate, Long likeCnt, String reviewAuthor, String reviewAuthorProfileImg, LocalDateTime reviewCreatedAt, LocalDateTime reviewUpdatedAt) {
        this.reviewId = reviewId;
        this.shopName = shopName;
        this.shopStatus = shopStatus;
        this.shopAddress = shopAddress;
        this.reviewReportStatus = reviewReportStatus;
        this.categoryTypeList = categoryTypeList;
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
