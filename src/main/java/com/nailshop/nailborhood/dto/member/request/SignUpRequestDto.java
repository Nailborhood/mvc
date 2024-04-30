package com.nailshop.nailborhood.dto.member.request;

import com.nailshop.nailborhood.type.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private LocalDate birthday;
    private String phoneNum;
    private Gender gender;
    private String nickname;
}
