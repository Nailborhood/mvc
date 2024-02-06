package com.nailshop.nailborhood.dto.shop.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ShopImgListResponseDto {

    private String imgPath;
    private int imgNum;

    public ShopImgListResponseDto(String imgPath, int imgNum) {

        this.imgPath = imgPath;
        this.imgNum = imgNum;
    }
}
