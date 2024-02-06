package com.nailshop.nailborhood.dto.artboard;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArtUpdateRequestDto {

    private String name;
    private String content;
    private List<Long> categoryIdList;
}
