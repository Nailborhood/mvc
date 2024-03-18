package com.nailshop.nailborhood.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 멤버
    CUSTOMER_NOT_FOUND("고객을 찾을 수 없습니다."),

    // 이미지 업로드
    IMAGE_UPLOAD_FAIL("이미지 업로드 실패"),
    FILE_EXTENSION_NOT_FOUND("이미지를 찾을 수 없습니다"),
    // 카테고리
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    // 아트판
    ART_NOT_FOUND("아트판 정보를 찾을 수 없습니다."),
    ART_REGISTRATION_FAIL("아트판 등록에 실패하였습니다."),
    ART_UPDATE_FAIL("아트판 수정에 실패하였습니다."),
    ART_DELETE_FAIL("아트판 삭제에 실패하였습니다."),
    // 매장
    SHOP_NOT_FOUND("매장 정보를 찾을 수 없습니다"),
    SHOPSTATUS_NOT_FOUND("매장 상태 정보를 찾을 수 없습니다"),

    // Member
    MEMBER_NOT_FOUND("유저 정보 조회에 실패하였습니다."),
    EMAIL_NOT_AVAILABLE("사용할 수 없는 이메일입니다."),
    NICKNAME_NOT_AVAILABLE("이미 사용 중인 닉네임입니다."),
    PHONENUM_NOT_AVAILABLE("이미 사용 중인 전화번호입니다."),
    PASSWORD_NOT_MATCH_WITH_PATTERN("비밀번호 형식과 맞지 않는 비밀번호 입니다."),
    SIGNUP_FAIL("회원가입에 실패하였습니다."),
    DROPOUT_FAIL("회원탈퇴에 실패하였습니다."),
    DROPOUT_ALREADY("이미 탈퇴한 회원입니다."),
    AUTHOR_NOT_EQUAL("작성자가 일치하지 않습니다."),

    LOGIN_FAIL("아이디 혹은 비밀번호가 일치하지 않습니다."),
    LOGOUT_FAIL("로그아웃에 실패하였습니다."),

    MYINFO_FAIL("내 정보 조회에 실패하였습니다."),
    MYINFO_UPDATE_FAIL("내 정보 수정에 실패하였습니다."),
    PASSWORD_CHECK_FAIL("비밀번호가 일치하지 않습니다."),
    PASSWORD_UPDATE_FAIL("비밀번호 수정에 실패하였습니다."),
    PROFILE_UPDATE_FAIL("프로필 업로드에 실패하였습니다."),

    UNAUTHORIZED_ACCESS("접근 권한이 없습니다."),

    // 리뷰
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다."),
    REVIEW_REPORT_NOT_FOUND("신고된 리뷰를 찾을 수 없습니다"),
    REVIEW_REPORT_INCORRECT("리뷰 신고 상태가 올바르지 않습니다"),

    //주소(동)
    DONG_NOT_FOUND("서비스 지역이 아닙니다. 다른 지역을 선택해주세요"),

    //리뷰
    REVIEW_NOT_REGISTRATION("등록된 리뷰가 없습니다"),
    ART_NOT_REGISTRATION("등록된 아트판이 없습니다"),
    SHOP_NOT_REGISTRATION("등록된 가게가 없습니다.");

    private final String description;
}
