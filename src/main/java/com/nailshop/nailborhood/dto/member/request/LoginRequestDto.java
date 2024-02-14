package com.nailshop.nailborhood.dto.member.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @NotNull
    private String email;
    @NotNull
    private String password;
}