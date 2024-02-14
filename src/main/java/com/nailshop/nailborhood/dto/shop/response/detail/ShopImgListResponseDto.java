package com.nailshop.nailborhood.dto.shop.response.detail;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopImgListResponseDto {

    private String imgPath;
    private int imgNum;

    @Builder
    public ShopImgListResponseDto(String imgPath, int imgNum) {

        this.imgPath = imgPath;
        this.imgNum = imgNum;
    }
}
