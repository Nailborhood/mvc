package com.nailshop.nailborhood.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDto {
    @NotNull
    private String email;
    @NotNull
    private String password;
}