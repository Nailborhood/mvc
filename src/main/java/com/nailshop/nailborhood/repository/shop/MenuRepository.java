package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {

    // 매장 shopId에 해당되는 메뉴 삭제
    @Modifying
    @Query("DELETE FROM Menu m WHERE m.shop.shopId = :shopId")
    void deleteAllByShopId(@Param("shopId") Long shopId);
}
