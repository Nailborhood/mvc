package com.nailshop.nailborhood.dto.shop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopMenuDto {
    private String name;
    private String price;

}
