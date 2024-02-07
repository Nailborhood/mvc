package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 이미지 업로드
    IMAGE_UPLOAD_FAIL("이미지 업로드 실패"),
    FILE_EXTENSION_NOT_FOUND("이미지를 찾을 수 없습니다"),
    // 카테고리
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    // 아트판
    ART_NOT_FOUND("아트판 정보를 찾을 수 없습니다."),
    // 매장
    SHOP_NOT_FOUND("매장 정보를 찾을 수 없습니다"),
    SHOPSTAUTS_NOT_FOUND("매장 상태 정보를 찾을 수 없습니다"),
    //주소(동)
    DONG_NOT_FOUND("서비스 지역이 아닙니다. 다른 지역을 선택해주세요"),
    //리뷰
    REVIEW_NOT_REGISTRATION("등록된 리뷰가 없습니다"),
    ART_NOT_REGISTRATION("등록된 아트판이 없습니다");

    private final String description;
}
