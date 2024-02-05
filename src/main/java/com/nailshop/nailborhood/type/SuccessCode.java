package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    EXAMPLE_SUCCESS("성공 예시 코드 입니다."),
    SHOP_REGISTRATION_SUCCESS("매장 등록을 성공했습니다."),
    SHOP_MODIFICATION_SUCCESS("매장 수정을 성공했습니다."),
    ALL_SHOP_LOOKUP_SUCCESS("매장 조회를 성공했습니다"),
    SHOP_DETAIL_LOOKUP_SUCCESS("매장 상세 조회를 성공했습니다"),
    REVIEW_REGISTRATION_SUCCESS("리뷰 등록을 성공했습니다"),


    // 멤버
    EMAIL_AVAILABLE("사용 가능한 이메일입니다"),
    EMAIL_NOT_AVAILABLE("중복된 이메일입니다");

    private final String description;
}
