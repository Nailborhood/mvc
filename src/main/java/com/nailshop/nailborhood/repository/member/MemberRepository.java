package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByPhoneNum(String phoneNum);

    @Query("SELECT m FROM Member m " +
            "WHERE m.email = :email " +
            "AND m.password = :password " +
            "AND m.isDeleted = false")
    Optional<Member> findExistMember(String email, String password);

    @Query("SELECT m " +
            "FROM Member m " +
            "WHERE m.memberId = :memberId AND m.isDeleted = false ")
    Optional<Member> findByMemberIdAndIsDeleted(@Param("memberId") Long memberId);

    Page<Member> findAll(Pageable pageable);
}