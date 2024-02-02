package com.nailshop.nailborhood.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopStatus {
    BEFORE_OPEN("SHOP_STATUS_BEFORE_OPEN", "영업 전"),
    OPEN("SHOP_STATUS_OPEN","운영 중"),
    CLOSED("SHOP_STATUS_CLOSED","영업 종료");

    private final String key;
    private final String title;
}
