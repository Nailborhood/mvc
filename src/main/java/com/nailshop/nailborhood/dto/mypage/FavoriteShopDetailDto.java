package com.nailshop.nailborhood.dto.mypage;

import com.nailshop.nailborhood.domain.shop.Shop;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteShopDetailDto {

    private Long shopId;
    private String shopName;
    private String mainImgPath;
    private int favoriteCnt;
    private Boolean isDeleted;
    private double rateAvg;
    private int reviewCnt;
    private String address;


    @Builder
    public FavoriteShopDetailDto(Long shopId, String shopName, String mainImgPath, int favoriteCnt, Boolean isDeleted, double rateAvg, int reviewCnt, String address) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.mainImgPath = mainImgPath;
        this.favoriteCnt = favoriteCnt;
        this.isDeleted = isDeleted;
        this.rateAvg = rateAvg;
        this.reviewCnt = reviewCnt;
        this.address = address;
    }

}
