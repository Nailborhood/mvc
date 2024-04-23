package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    @Query("SELECT ad " +
            "FROM Admin ad " +
            "LEFT JOIN ad.member m " +
            "WHERE m.memberId =:memberId AND m.isDeleted = false ")
    Admin findByMemberId(@Param("memberId") Long memberId);
}
