package com.nailshop.nailborhood.domain.member;

import com.nailshop.nailborhood.domain.alarm.Alarm;
import com.nailshop.nailborhood.domain.artboard.ArtLike;
import com.nailshop.nailborhood.domain.common.BaseTime;
import com.nailshop.nailborhood.domain.review.ReviewLike;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memeber_id")
    private Long memberId;

    private String email;

    private String name;

    private String password;

    private LocalDate birthday;

    @Column(name = "phone_num")
    private String phoneNum;

    private String gender;

    private String nickname;

    @Column(name = "profile_img")
    private String profileImg;

    private String provider;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private Role role;



    @OneToOne(mappedBy = "member")
    private Customer customer;
    @OneToOne(mappedBy = "member")
    private Owner owner;
    @OneToOne(mappedBy = "member")
    private Admin admin;
    @OneToOne(mappedBy = "member")
    private Login login;
    //TODO: alarm 맵핑
    @OneToMany(mappedBy = "receiver")
    private List<Alarm> receivedAlarms;  // 받은 알람 목록

    @OneToMany(mappedBy = "sender")
    private List<Alarm> sentAlarms;  // 보낸 알람 목록

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Favorite> FavoriteList;
    @OneToMany(mappedBy = "member")
    private List<ArtLike> artLikeList;

    @OneToMany(mappedBy = "member")
    private List<ReviewLike> reviewLikeList;

    @OneToMany(mappedBy = "member")
    private List<ReviewReport> reviewReportList;

    @Builder
    public Member( String email, String name, String password, LocalDate birthday, String phoneNum, String gender, String nickname, String profileImg,  String provider, boolean isDeleted, Role role) {

        this.email = email;
        this.name = name;
        this.password = password;
        this.birthday = birthday;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.provider = provider;
        this.isDeleted = isDeleted;
        this.role = role;
    }

    public void changeRole(Role role){
        this.role = role;
    }
}
