package com.nailshop.nailborhood.dto.shop.response.detail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopCertificateImgResponseDto {
    private String imgPath;
    private int imgNum;
}
