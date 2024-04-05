package com.nailshop.nailborhood.dto.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ModMemberInfoRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String phoneNum;
    private String gender;
    private String nickname;

    @Builder
    public ModMemberInfoRequestDto(LocalDate birthday, String phoneNum, String gender, String nickname) {
        this.birthday = birthday;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.nickname = nickname;
    }
}
