package com.nailshop.nailborhood.dto.shop.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopMenuDto {
    private String name;
    private String price;

}
