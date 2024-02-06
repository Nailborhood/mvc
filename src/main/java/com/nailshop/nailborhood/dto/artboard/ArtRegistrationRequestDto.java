package com.nailshop.nailborhood.dto.artboard;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArtRegistrationRequestDto {

    private String name;
    private String content;
    private Long shopId;
    private List<Long> categoryIdList;
}
