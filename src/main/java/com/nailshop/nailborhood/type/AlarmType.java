package com.nailshop.nailborhood.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    REVIEW("리뷰"),
    FAVORITE("찜"),
    LIKE_ART("좋아요"),
    CHAT("채팅");

    //TODO: 다른 부분 추가 예정
    private final String description;

    // 문자열로부터 AlarmType 을 반환하는 메소드
    public static AlarmType fromString(String description) {
        for (AlarmType type : AlarmType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown alarm type: " + description);
    }
}
