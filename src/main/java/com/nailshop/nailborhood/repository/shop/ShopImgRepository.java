package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.shop.response.ShopImgListResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopImgRepository extends JpaRepository<ShopImg,Long> {

    // shopId로 shopImg 리스트 불러오기
    @Query("SELECT si " +
            "FROM ShopImg si " +
            "LEFT JOIN si.shop s " +
            "WHERE s.shopId = :shopId")
    List<ShopImg> findByShopImgListByShopId(@Param("shopId") Long shopId);

    // shopImg 삭제
    @Modifying
    @Query("DELETE FROM ShopImg si WHERE si.shop.shopId = :shopId")
    void deleteByShopId(@Param("shopId") Long shopId);

    // 매장 이미지 중 대표 이미지 불러오기
    @Query("SELECT si.imgPath " +
            "FROM ShopImg si " +
            "LEFT JOIN si.shop s " +
            "WHERE s.shopId =:shopId AND si.imgNum = 1 ")
    String findByShopImgByShopIdAndShopImgId(@Param("shopId")Long shopId);

    // shopId에 해당되는 매장 이미지 리스트 불러오기
    @Query("SELECT NEW com.nailshop.nailborhood.dto.shop.response.ShopImgListResponseDto(" +
            "si.imgPath AS imgPath," +
            "si.imgNum AS imgNum)" +
            "FROM ShopImg  si " +
            "LEFT JOIN si.shop s " +
            "WHERE s.shopId = :shopId ")
    List<ShopImgListResponseDto> findAllByShopImgListByShopId(@Param("shopId")Long shopId);

    @Query("UPDATE ShopImg  si " +
            "SET si.isDeleted = :status " +
            "WHERE si.shopImgId = :shopImgId")
    @Modifying(clearAutomatically = true)
    void deleteByShopImgId(@Param("shopImgId") Long shopImgId, @Param("status") boolean status);

}
