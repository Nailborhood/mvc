package com.nailshop.nailborhood.domain.member;

import com.nailshop.nailborhood.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private String name;

    private LocalDateTime birthday;

    private String phoneNum;

    private String gender;

    private String address;

    private String nickname;

    private String profileImg;

    private LocalDateTime joinDate;

    private String provider;

    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member( String email, String name, LocalDateTime birthday, String phoneNum, String gender, String address, String nickname, String profileImg, LocalDateTime joinDate, String provider, boolean isDeleted, Role role) {

        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.address = address;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.joinDate = joinDate;
        this.provider = provider;
        this.isDeleted = isDeleted;
        this.role = role;
    }
}
