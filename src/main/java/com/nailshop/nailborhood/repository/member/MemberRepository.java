package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    Optional<Member> findExistMember(@Param("email") String email,@Param("password") String password);

    @Modifying
    @Query("UPDATE Member m SET m.address = :address, " +
            "m.phoneNum = :phoneNum, " +
            "m.nickname = :nickname, " +
            "m.birthday = :birthday, " +
            "m.gender = :gender "+
            "WHERE m.memberId = :id")
    void updateMemberByMemberId(
            @Param("id") Long id, @Param("address") String address, @Param("nickname") String nickname,
            @Param("phoneNum") String phoneNum, @Param("gender") String gender,@Param("birthday") LocalDate birthday);

    @Modifying
    @Query("UPDATE Member m SET " +
            "m.password = :password "+
            "WHERE m.memberId = :id")
    void updateMemberPasswordByMemberId(
            @Param("id") Long id, @Param("password") String password);

    @Modifying
    @Query("UPDATE Member m SET " +
            "m.isDeleted = true "+
            "WHERE m.memberId = :id")
    void updateMemberIsDeletedById(@Param("id") Long id);

}