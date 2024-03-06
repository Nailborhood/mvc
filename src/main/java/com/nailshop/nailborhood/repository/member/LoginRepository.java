package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByMember_MemberId(Long memberId);
    Optional<Login> findByRefreshToken(String refreshToken);


    @Modifying
    @Query("UPDATE Login l SET " +
            "l.refreshToken = :refreshToken "+
            "WHERE l.member.memberId = :memberId")
    void updateRefreshTokenByMemberId(@Param("memberId") Long memberId, @Param("refreshToken") String refreshToken);
}
