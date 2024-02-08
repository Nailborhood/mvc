package com.nailshop.nailborhood.type;

import com.fasterxml.jackson.databind.JsonMappingException;
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

    // 리뷰
    REVIEW_REGISTRATION_SUCCESS("리뷰 등록을 성공했습니다"),
    REVIEW_UPDATE_SUCCESS("리뷰 수정에 성공했습니다."),
    REVIEW_REPORT_SUCCESS("리뷰 신고에 성공했습니다."),
    REVIEW_DELETE_SUCCESS("리뷰 삭제에 성공했습니다."),
    REVIEW_LIKE_SUCCESS("리뷰 공감에 성공했습니다."),
    REVIEW_LIKE_CANCEL_SUCCESS("리뷰 공감을 취소했습니다."),
    REVIEW_INQUIRY_SUCCESS("리뷰 상세조회에 성공했습니다.");




    private final String description;
}
