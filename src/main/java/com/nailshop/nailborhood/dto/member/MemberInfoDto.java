package com.nailshop.nailborhood.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class MemberInfoDto {

    private String email;
    private String name;
    private LocalDate birthday;
    private String phoneNum;
    private String gender;
    private String address;
    private String nickname;
    private String profileImg;
    private boolean isDeleted;
    private LocalDateTime createdAt;

    @Builder
    public MemberInfoDto(String email, String name, LocalDate birthday, String phoneNum, String gender, String address, String nickname, String profileImg, boolean isDeleted, LocalDateTime createdAt) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.address = address;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
