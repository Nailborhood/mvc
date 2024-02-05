package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    EXAMPLE_SUCCESS("성공 예시 코드 입니다."),

    // 리뷰
    REVIEW_UPDATE_SUCCESS("리뷰 수정에 성공했습니다."),
    REVIEW_REPORT_SUCCESS("리뷰 신고에 성공했습니다.");



    private final String description;
}
