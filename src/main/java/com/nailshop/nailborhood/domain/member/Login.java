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
    private Long loginId;

    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Login(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
