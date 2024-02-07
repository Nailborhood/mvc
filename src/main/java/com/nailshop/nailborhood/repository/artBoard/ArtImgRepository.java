package com.nailshop.nailborhood.repository.artBoard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtImgRepository extends JpaRepository<ArtImg,Long> {
    // 매장에 해당 하는 리뷰 대표 이미지 불러오기
    @Query("SELECT ai.imgPath " +
            "FROM ArtImg ai " +
            "LEFT JOIN ai.artRef a " +
            "LEFT JOIN a.shop s " +
            "WHERE a.artRefId = :artRefId AND s.shopId = :shopId AND ai.imgNum = 1")
    String findArtImgByShopIdAndArtRefId(@Param("shopId") Long shopId, @Param("artRefId") Long artRefId);
}
