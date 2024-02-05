package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 이미지 업로드
    IMAGE_UPLOAD_FAIL("이미지 업로드 실패"),
    FILE_EXTENSION_NOT_FOUND("이미지를 찾을 수 없습니다"),
    // 매장
    SHOP_NOT_FOUND("매장 정보를 찾을 수 없습니다"),
    SHOPSTAUTS_NOT_FOUND("매장 상태 정보를 찾을 수 없습니다"),


    // 리뷰
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다.");

    private final String description;
}
