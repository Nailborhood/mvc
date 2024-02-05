package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    EXAMPLE_SUCCESS("성공 예시 코드 입니다."),



    // 멤버
    EMAIL_AVAILABLE("사용 가능한 이메일입니다"),
    EMAIL_NOT_AVAILABLE("중복된 이메일입니다");

    private final String description;
}
