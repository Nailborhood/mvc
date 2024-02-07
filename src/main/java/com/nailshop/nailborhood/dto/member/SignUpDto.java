package com.nailshop.nailborhood.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private LocalDateTime birthday;
    private String phoneNum;
    private String gender;
    private String address;
    private String nickname;
}
