package com.nailshop.nailborhood.dto.member.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponseDto {
    private boolean status;

    public FavoriteResponseDto(boolean status) {
        this.status = status;
    }
}
