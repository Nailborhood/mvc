package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EXAMPLE_EXCEPTION("에러 예시 코드 입니다."),


    // Member
    MEMBER_NOT_FOUND("유저 정보 조회에 실패하였습니다."),
    EMAIL_NOT_AVAILABLE("사용할 수 없는 이메일입니다."),
    NICKNAME_NOT_AVAILABLE("이미 사용 중인 닉네임입니다."),
    PHONENUM_NOT_AVAILABLE("이미 사용 중인 전화번호입니다."),
    PASSWORD_NOT_MATCH_WITH_PATTERN("비밀번호 형식과 맞지 않는 비밀번호 입니다."),
    SIGNUP_FAIL("회원가입에 실패하였습니다.")
    ;

    private final String description;
}
