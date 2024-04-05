package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    @Query("UPDATE Owner  o " +
            "SET o.isApproved = :status " +
            "WHERE o.ownerId = :ownerId ")
    @Modifying(clearAutomatically = true)
    void deleteByOwnerId(@Param("ownerId") Long ownerId, @Param("status") boolean status);

    @Query("SELECT o " +
            "FROM Owner o " +
            "WHERE o.ownerId =:ownerId")
    Optional<Owner> findByOwnerId(@Param("ownerId") Long ownerId);
}
