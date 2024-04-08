package com.nailshop.nailborhood.dto.member.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ModPasswordRequestDto {
    private String oldPassword;
    private String newPassword;
    private String newPasswordCheck;

}
