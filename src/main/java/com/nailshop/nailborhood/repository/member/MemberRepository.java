package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByPhoneNum(String phoneNum);

    // 내 정보 수정
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

    // 비밀번호 수정
    @Modifying
    @Query("UPDATE Member m SET " +
            "m.password = :password "+
            "WHERE m.memberId = :id")
    void updateMemberPasswordByMemberId(
            @Param("id") Long id, @Param("password") String password);

    // 회원 탈퇴 처리
    @Modifying
    @Query("UPDATE Member m SET " +
            "m.isDeleted = true "+
            "WHERE m.memberId = :id")
    void updateMemberIsDeletedById(@Param("id") Long id);

    // 프로필 사진 업로드
    @Modifying
    @Query("UPDATE Member m SET " +
            "m.profileImg = :imgURL "+
            "WHERE m.memberId = :id")
    void updateMemberProfileImg(@Param("id") Long id, @Param("imgURL") String imgURL);

    @Query("SELECT m " +
            "FROM Member m " +
            "WHERE m.memberId = :memberId AND m.isDeleted = false ")
    Optional<Member> findByMemberIdAndIsDeleted(@Param("memberId") Long memberId);

    Page<Member> findAll(Pageable pageable);

    // 관리자 회원 검색
    @Query("SELECT m " +
            "FROM Member m " +
            "where m.name LIKE %:keyword")
    Page<Member> findAllMemberListByKeyword(@Param("keyword")String keyword, Pageable pageable);
}