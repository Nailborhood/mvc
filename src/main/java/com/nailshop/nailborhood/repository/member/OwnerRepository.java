package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT o " +
            "FROM Owner o " +
            "LEFT JOIN o.member m " +
            "WHERE m.memberId =:memberId ")
    Owner findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT o " +
            "FROM Owner o " +
            "LEFT JOIN o.shop s " +
            "WHERE s.shopId =:shopId AND s.isDeleted = false ")
    Owner findByShopId(@Param("shopId") Long shopId);
}
