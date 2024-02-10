package com.nailshop.nailborhood.repository.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtImgRepository extends JpaRepository<ArtImg, Long> {

    @Query("SELECT ai " +
            "FROM ArtImg ai " +
            "LEFT JOIN ai.artRef a " +
            "WHERE a.artRefId = :artRefId ")
    List<ArtImg> findByArtRefId(@Param("artRefId") Long artRefId);

    @Modifying
    @Query("DELETE FROM ArtImg ai WHERE ai.artRef.artRefId = :artRefId")
    void deleteByArtRefId(@Param("artRefId") Long ArtRefId);

    @Query("UPDATE ArtImg ai " +
            "SET ai.isDeleted = :status " +
            "WHERE ai.artImgId = :artImgId ")
    @Modifying(clearAutomatically = true)
    void deleteByArtImgId(@Param("artImgId") Long artImgId ,@Param("status") Boolean status);

    // 매장에 해당 하는 리뷰 대표 이미지 불러오기
    @Query("SELECT ai.imgPath " +
            "FROM ArtImg ai " +
            "LEFT JOIN ai.artRef a " +
            "LEFT JOIN a.shop s " +
            "WHERE a.artRefId = :artRefId AND s.shopId = :shopId AND ai.imgNum = 1")
    String findArtImgByShopIdAndArtRefId(@Param("shopId") Long shopId, @Param("artRefId") Long artRefId);




}
