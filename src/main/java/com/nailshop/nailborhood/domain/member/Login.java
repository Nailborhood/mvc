package com.nailshop.nailborhood.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long loginId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Login(String refreshToken, Member member) {
        this.refreshToken = refreshToken;
        this.member = member;
    }

    // refresh token 업데이트
    public Login updateToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }

}
