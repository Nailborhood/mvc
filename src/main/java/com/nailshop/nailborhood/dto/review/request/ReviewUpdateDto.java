package com.nailshop.nailborhood.dto.review.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewUpdateDto {

    private String contents;
    private Integer rate;
    private List<Long> categoryListId;

}