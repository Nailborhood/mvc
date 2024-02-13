package com.nailshop.nailborhood.dto.review;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateDto {

    private String contents;
    private Integer rate;

}