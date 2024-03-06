package com.nailshop.nailborhood.dto.shop.response.detail;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuDetailResponseDto {
    private Long menuId;
    private String name;
    private String price;

    @Builder
    public MenuDetailResponseDto(Long menuId, String name, String price) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
    }
}
