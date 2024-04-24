package com.nailshop.nailborhood.dto.member.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class UserInfoResponseDto {
    private Long memberId;
    private String nickname;
    private Long reviewCnt;
    private Long favoriteCnt;
    private Long reservationCnt;
}
