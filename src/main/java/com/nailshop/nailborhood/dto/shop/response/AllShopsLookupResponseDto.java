package com.nailshop.nailborhood.dto.shop.response;

import com.nailshop.nailborhood.type.ShopStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class AllShopsLookupResponseDto {
    private Long shopId;
    private String name;
    private String phone;
    private String address;
    private String opentime;
    private String website;
    private String content;
    private ShopStatus status;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private Integer reviewCnt; // 리뷰 개수
    private Integer favoriteCnt; // 좋아요 개수
    private double rateAvg;
    private long menuCnt; // 메뉴 개수


    public AllShopsLookupResponseDto(Long shopId, String name, String phone, String address, String opentime, String website, String content, ShopStatus status, Boolean isDeleted, LocalDateTime createdAt, Integer reviewCnt, Integer favoriteCnt, double rateAvg, long menuCnt) {
        this.shopId = shopId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.opentime = opentime;
        this.website = website;
        this.content = content;
        this.status = status;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.reviewCnt = reviewCnt;
        this.favoriteCnt = favoriteCnt;
        this.rateAvg = rateAvg;
        this.menuCnt = menuCnt;
    }
}
