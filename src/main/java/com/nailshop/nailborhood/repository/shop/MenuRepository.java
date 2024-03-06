package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.Menu;
import com.nailshop.nailborhood.dto.shop.response.detail.MenuDetailResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {

    // 매장 shopId에 해당되는 메뉴 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Menu m WHERE m.shop.shopId = :shopId")
    void deleteAllByShopId(@Param("shopId") Long shopId);

    // 매장 menu 개수
    @Query("SELECT count(m) " +
            "FROM Menu m " +
            "LEFT JOIN m.shop s "+
            "WHERE s.shopId = :shopId")
    long countByShopId(@Param("shopId") Long shopId);

    // 매장 메뉴 리스트 불러오기
    @Query("SELECT NEW com.nailshop.nailborhood.dto.shop.response.detail.MenuDetailResponseDto(" +
            "m.menuId AS menuId," +
            "m.name AS menu," +
            "m.price AS price) " +
            "FROM Menu m " +
            "LEFT JOIN m.shop s " +
            "WHERE s.shopId = :shopId")
    List<MenuDetailResponseDto> findAllByShopId(@Param("shopId") Long shopId);
}
