package com.nailshop.nailborhood.type;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    EXAMPLE_SUCCESS("성공 예시 코드 입니다."),
    // 매장
    SHOP_REGISTRATION_SUCCESS("매장 등록을 성공했습니다."),
    SHOP_MODIFICATION_SUCCESS("매장 수정을 성공했습니다."),
    // 아트판
    ART_REGISTRATION_SUCCESS("아트판 등록에 성공했습니다."),
    ART_UPDATE_SUCCESS("아트판 수정에 성공했습니다."),
    ART_DELETE_SUCCESS("아트판 삭제에 성공했습니다.");


    private final String description;
}
