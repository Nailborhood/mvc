package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtLikeResponseDto {

    private Boolean status;

    @Builder
    public ArtLikeResponseDto(Boolean status) {
        this.status = status;
    }
}
