package com.nailshop.nailborhood.dto.alarm;

import com.nailshop.nailborhood.type.AlarmType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlarmRequestDto {

    private AlarmType alarmType; // 알람 종류 ex. 리뷰 알람
    private String receiver; // 알람 수신자
    private String url; // 알람 출처 url

}
