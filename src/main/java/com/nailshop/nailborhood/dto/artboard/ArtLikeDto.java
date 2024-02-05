package com.nailshop.nailborhood.dto.artboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtLikeDto {

    private Boolean status;

    @Builder
    public ArtLikeDto(Boolean status) {
        this.status = status;
    }
}
