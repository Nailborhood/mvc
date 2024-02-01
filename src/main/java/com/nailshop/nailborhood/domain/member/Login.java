package com.nailshop.nailborhood.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Builder
    public Login(Long loginId, String refreshToken) {
        this.loginId = loginId;
        this.refreshToken = refreshToken;
    }
}
