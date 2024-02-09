package com.nailshop.nailborhood.type;

import com.nailshop.nailborhood.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopStatus {
    BEFORE_OPEN("영업전"),
    OPEN("운영중"),
    CLOSED("영업종료");


    private final String description;

}
