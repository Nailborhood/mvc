package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop,Long> {

    // 매장 ShopId로 존재 여부 확인 (isDeleted = false)
    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE s.shopId =:shopId AND s.isDeleted = false ")
    Optional<Shop> findByShopIdAndIsDeleted(@Param("shopId")Long shopId);

    @Query("SELECT s FROM Shop s WHERE s.isDeleted = false")
    Page<Shop> findAllNotDeleted(Pageable pageable);

    @Query("SELECT s " +
            "FROM Shop s " +
            "left JOIN s.dong d " +
            "WHERE s.isDeleted = false AND d.dongId =:dongId")
    Page<Shop> findAllNotDeletedByDongId(Pageable pageable, @Param("dongId") Long dongId);
}
