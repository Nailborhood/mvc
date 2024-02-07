package com.nailshop.nailborhood.dto.shop.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class MenuDetailResponseDto {
    private Long menuId;
    private String name;
    private String price;

    public MenuDetailResponseDto(Long menuId, String name, String price) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
    }
}
