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

    SHOP_ART_LOOKUP_SUCCESS("매장 아트판 조회를 성공했습니다"),
    SHOP_REVIEW_LOOKUP_SUCCESS("매장 리뷰 조회를 성공했습니다"),
    FAVORITE_SAVE_SUCCESS("관심 매장에 추가 했습니다"),
    FAVORITE_CANCEL_SUCCESS("관심 매장에서 삭제 했습니다"),
    SHOP_DELETE_SUCCESS("매장을 삭제했습니다"),
    SHOP_STATUS_BEFORE_OPEN("매장 상태:'영업전'으로 변경되었습니다"),
    SHOP_STATUS_OPEN("매장 상태:'영업중'으로 변경되었습니다"),
    SHOP_STATUS_CLOSED("매장 상태:'운영종료'로 변경되었습니다"),

    // 리뷰
    REVIEW_REGISTRATION_SUCCESS("리뷰 등록을 성공했습니다"),
    REVIEW_UPDATE_SUCCESS("리뷰 수정에 성공했습니다."),
    REVIEW_REPORT_SUCCESS("리뷰 신고에 성공했습니다."),
    REVIEW_DELETE_SUCCESS("리뷰 삭제에 성공했습니다."),
    REVIEW_LIKE_SUCCESS("리뷰 공감에 성공했습니다."),
    REVIEW_LIKE_CANCEL_SUCCESS("리뷰 공감을 취소했습니다."),
    REVIEW_DETAIL_INQUIRY_SUCCESS("리뷰 상세조회에 성공했습니다."),
    REVIEW_INQUIRY_SUCCESS("리뷰 전체조회에 성공했습니다."),
    All_REVIEW_REPORT_SUCCESS("리뷰 신고 조회에 성공했습니다"),
    REVIEW_REPORT_STATUS_REJECT_SUCCESS("리뷰 신고가 반려 처리되었습니다"),
    REVIEW_REPORT_STATUS_ACCEPT_SUCCESS("리뷰 신고 처리되었습니다"),

    MY_REVIEW_INQUIRY_SUCCESS("내가 작성한 리뷰들 조회에 성공했습니다."),
    MY_FAVORITE_SHOP_INQUIRY_SUCCESS("내가 찜한 매장들 조회에 성공했습니다."),


    // 검색
    SEARCH_BY_REVIEW_SUCCESS("리뷰 검색에 성공했습니다."),
    SEARCH_BY_ART_SUCCESS("아트판 검색에 성공했습니다."),
    SEARCH_BY_SHOP_SUCCESS("매장 검색에 성공했습니다."),
    SEARCH_BY_CHATROOM_SUCCESS("채팅 검색에 성공했습니다"),



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
    DROPOUT_SUCCESS("회원탈퇴가 완료되었습니다."),

    LOGIN_SUCCESS("로그인에 성공하였습니다."),
    LOGOUT_SUCCESS("로그아웃에 성공하였습니다."),

    MYINFO_SUCCESS("내 정보 조회에 성공하였습니다."),
    MYINFO_UPDATE_SUCCESS("내 정보 수정에 성공하였습니다."),
    PASSWORD_CHECK_SUCCESS("비밀번호가 일치합니다."),
    PASSWORD_UPDATE_SUCCESS("비밀번호 수정에 성공하였습니다."),
    PROFILE_UPDATE_SUCCESS("프로필 업로드에 성공하였습니다."),


    //관리자
    CHANGE_ROLE_SUCCESS("유저 권한 변경에 성공하였습니다."),
    MEMBER_ALL_INQUIRY_SUCCESS("유저 전체 조회에 성공하였습니다."),
    APPROVE_SHOP_REGISTRATION("매장등록신청 승인에 성공하였습니다."),
    REJECT_SHOP_REGISTRATION("매장등록신청 거절에 성공하였습니다.")

    ;

    private final String description;
}
