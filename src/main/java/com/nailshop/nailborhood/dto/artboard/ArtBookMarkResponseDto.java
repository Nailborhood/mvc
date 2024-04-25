package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtBookMarkResponseDto {

    private Boolean status;

    @Builder
    public ArtBookMarkResponseDto(Boolean status) {
        this.status = status;
    }
}
