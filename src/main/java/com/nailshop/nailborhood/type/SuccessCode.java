package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    EXAMPLE_SUCCESS("성공 예시 코드 입니다."),
    // 매장
    SHOP_REGISTRATION_SUCCESS("매장 등록을 성공했습니다."),
    SHOP_MODIFICATION_SUCCESS("매장 수정을 성공했습니다."),
    ALL_SHOP_LOOKUP_SUCCESS("매장 조회를 성공했습니다"),
    SHOP_DETAIL_LOOKUP_SUCCESS("매장 상세 조회를 성공했습니다"),
    REVIEW_REGISTRATION_SUCCESS("리뷰 등록을 성공했습니다"),

    SHOP_ART_LOOKUP_SUCCESS("매장 아트판 조회를 성공했습니다"),
    SHOP_REVIEW_LOOKUP_SUCCESS("매장 리뷰 조회를 성공했습니다"),
    FAVORITE_SAVE_SUCCESS("매장 찜 등록에 성공했습니다"),
    FAVORITE_CANCEL_SUCCESS("매장 찜을 취소 했습니다"),

    // 아트판
    ART_REGISTRATION_SUCCESS("아트판 등록에 성공했습니다."),
    ART_UPDATE_SUCCESS("아트판 수정에 성공했습니다."),
    ART_DELETE_SUCCESS("아트판 삭제에 성공했습니다."),
    ART_LIKE_SUCCESS("아트판 좋아요에 성공했습니다."),
    ART_DISLIKE_SUCCESS("아트판 좋아요 취소에 성공했습니다."),
    ART_ALL_INQUIRY_SUCCESS("아트판 전체 조회에 성공했습니다."),
    ART_INQUIRY_SUCCESS("아트판 상세 조회에 성공했습니다."),
  
    // 멤버
    MEMBER_FOUND("유저 정보 조회에 성공하였습니다."),
    EMAIL_AVAILABLE("사용 가능한 이메일입니다"),
    NICKNAME_AVAILABLE("사용 가능한 닉네임입니다."),
    PHONENUM_AVAILABLE("사용 가능한 전화번호입니다."),
    SIGNUP_SUCCESS("회원가입이 완료되었습니다."),

    LOGIN_SUCCESS("로그인에 성공하였습니다."),
    MYINFO_SUCCESS("내 정보 조회에 성공하였습니다.")
    ;

    private final String description;
}
