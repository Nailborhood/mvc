package com.nailshop.nailborhood.dto.alarm;

import com.nailshop.nailborhood.type.AlarmType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmResponseDto {
    private Long alarmId;
    private AlarmType alarmType; // 알람 종류 ex. 리뷰 알람
    private boolean isChecked; // 알람 수신 확인
    private Long receiverId; // 알람 수신자
    private Long senderId; // 알람 발신자
    private String senderName; // 알람 발신자 이름
    private String url; // 알람 출처 url
    private String createdAt;
    private String shopName;
    private String artRefName;
    private String senderProfileImg; //  알람 보내는 사람 profile 이미지
}
