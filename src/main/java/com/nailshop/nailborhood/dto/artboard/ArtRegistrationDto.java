package com.nailshop.nailborhood.dto.artboard;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtRegistrationDto {

    private String name;
    private String content;
    private Long shopId;
    private Long categoryId;
}
