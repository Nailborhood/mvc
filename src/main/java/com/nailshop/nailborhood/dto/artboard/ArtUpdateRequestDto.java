package com.nailshop.nailborhood.dto.artboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ArtUpdateRequestDto {

    private String name;
    private String content;
    private List<Long> categoryIdList;
}
