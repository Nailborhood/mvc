package com.nailshop.nailborhood.dto.member.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ModPasswordRequestDto {
    private String password;
    private String passwordCheck;

}
