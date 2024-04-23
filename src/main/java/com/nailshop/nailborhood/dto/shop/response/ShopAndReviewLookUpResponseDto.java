package com.nailshop.nailborhood.dto.shop.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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

    private Long shopId;

    private List<Long> categoryIdList;

    private List<String> categoryTypeList;


    public ShopAndReviewLookUpResponseDto(Long reviewId, String contents, Integer rate, String reviewImgPath, LocalDateTime createdAt, String nickName, String shopName, String shopAddress, String time, double reviewAvg, int reviewCnt, int favoriteCnt, String shopMainImgPath, Long shopId, List<Long> categoryIdList, List<String> categoryTypeList) {
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
        this.shopId = shopId;
        this.categoryIdList = categoryIdList;
        this.categoryTypeList = categoryTypeList;
    }
}
