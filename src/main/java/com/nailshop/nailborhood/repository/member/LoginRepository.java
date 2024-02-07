package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByMember_MemberId(Long memberId);
    Optional<Login> findByRefreshToken(String refreshToken);
}
