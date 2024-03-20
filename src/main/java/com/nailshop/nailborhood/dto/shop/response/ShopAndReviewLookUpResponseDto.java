package com.nailshop.nailborhood.dto.shop.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ShopAndReviewLookUpResponseDto {
    private Long reviewId;

    private String contents;

    private Integer rate;

    private String reviewImgPath;

    private LocalDateTime createdAt;

    private String nickName;

    private String shopName;

    private String shopAddress;

    private String time;

    private double reviewAvg;

    private int reviewCnt;

    private int favoriteCnt;

    private String shopMainImgPath;

    // TODO 메뉴 추가


    public ShopAndReviewLookUpResponseDto(Long reviewId, String contents, Integer rate, String reviewImgPath, LocalDateTime createdAt, String nickName, String shopName, String shopAddress, String time, double reviewAvg, int reviewCnt, int favoriteCnt, String shopMainImgPath) {
        this.reviewId = reviewId;
        this.contents = contents;
        this.rate = rate;
        this.reviewImgPath = reviewImgPath;
        this.createdAt = createdAt;
        this.nickName = nickName;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.time = time;
        this.reviewAvg = reviewAvg;
        this.reviewCnt = reviewCnt;
        this.favoriteCnt = favoriteCnt;
        this.shopMainImgPath = shopMainImgPath;
    }
}
