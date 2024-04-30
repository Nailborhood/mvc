package com.nailshop.nailborhood.dto.member;

import com.nailshop.nailborhood.type.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class MemberInfoDto {

    private Long memberId;
    private String email;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String phoneNum;
    private Gender gender;
    private String address;
    private String nickname;
    private String profileImg;
    private boolean isDeleted;
    private LocalDateTime createdAt;

    @Builder
    public MemberInfoDto(Long memberId, String email, String name, LocalDate birthday, String phoneNum, Gender gender, String address, String nickname, String profileImg, boolean isDeleted, LocalDateTime createdAt) {
        this.memberId = memberId;
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
