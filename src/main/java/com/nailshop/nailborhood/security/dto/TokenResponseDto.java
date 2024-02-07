package com.nailshop.nailborhood.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class TokenResponseDto {
    private Long memberId;
    private String accessToken;
    private LocalDateTime accessTokenExpireTime;

    @Builder
    public TokenResponseDto(Long memberId, String accessToken, LocalDateTime accessTokenExpireTime) {
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.accessTokenExpireTime = accessTokenExpireTime;
    }
}
