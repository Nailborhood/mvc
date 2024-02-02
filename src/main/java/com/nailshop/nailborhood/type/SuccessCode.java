package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    EXAMPLE_SUCCESS("성공 예시 코드 입니다."),
    SHOP_REGISTRATION_SUCCESS("매장 등록을 성공했습니다.");

    private final String description;
}
