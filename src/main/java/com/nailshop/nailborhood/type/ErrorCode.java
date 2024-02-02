package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EXAMPLE_EXCEPTION("에러 예시 코드 입니다."),
    IMAGE_UPLOAD_FAIL("이미지 업로드 실패"),
    FILE_EXTENSION_NOT_FOUND("이미지를 찾을 수 없습니다");

    private final String description;
}
