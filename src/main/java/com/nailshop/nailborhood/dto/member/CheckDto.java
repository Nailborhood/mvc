package com.nailshop.nailborhood.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckDto {
    private String check;
    private boolean isExist;
}