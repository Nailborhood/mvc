package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.ShopImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopImgRepository extends JpaRepository<ShopImg,Long> {
    @Query("SELECT si " +
            "FROM ShopImg si " +
            "LEFT JOIN si.shop s " +
            "WHERE s.shopId = :shopId ")
    List<ShopImg> findByShopImgListByShopId(@Param("shopId") Long shopId);

    @Modifying
    @Query("DELETE FROM ShopImg si WHERE si.shop.shopId = :shopId")
    void deleteByShopId(@Param("shopId") Long shopId);
}
