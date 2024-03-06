package com.nailshop.nailborhood.repository.member;

import com.nailshop.nailborhood.domain.member.Favorite;
import com.nailshop.nailborhood.domain.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {

    // memberId 와 shopId로 매장 찜 상태 불러오기
    @Query("SELECT f " +
            "FROM Favorite f " +
            "LEFT JOIN f.member m " +
            "LEFT JOIN f.shop s " +
            "WHERE m.memberId = :memberId AND s.shopId = :shopId")
    Favorite findByMemberIdAndShopId(@Param("memberId") Long memberId,@Param("shopId") Long shopId);

    // 매장 찜 상태 변경
    @Query("UPDATE Favorite f " +
            "SET f.status = :status " +
            "WHERE f.favoriteId = :favoriteId")
    @Modifying(clearAutomatically = true)
    void updateStatus(@Param("favoriteId") Long favoriteId, @Param("status") boolean newStatus);

    @Query("SELECT f " +
            "FROM Favorite f " +
            "LEFT JOIN f.shop s " +
            "WHERE  s.shopId = :shopId")
    List<Favorite> findAllByShopIdAndIsDeleted(@Param("shopId") Long shopId);

    // 내가 찜한 매장 조회
    @Query("SELECT f.shop " +
            "FROM Favorite f " +
            "LEFT JOIN f.member m " +
            "WHERE f.member.memberId = :memberId AND f.status = true ")
    Page<Shop> findFavoriteListByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
