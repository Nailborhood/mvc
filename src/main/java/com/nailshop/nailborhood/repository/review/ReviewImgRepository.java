package com.nailshop.nailborhood.repository.review;

import com.nailshop.nailborhood.domain.review.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewImgRepository extends JpaRepository<ReviewImg,Long> {

    // 매장에 해당 하는 리뷰 대표 이미지 불러오기
    @Query("SELECT ri.imgPath " +
            "FROM ReviewImg ri " +
            "LEFT JOIN ri.review r " +
            "LEFT JOIN r.shop s " +
            "WHERE r.reviewId = :reviewId AND s.shopId = :shopId AND ri.imgNum = 1")
    String findReviewImgByShopIdAndReviewId(@Param("shopId") Long shopId, @Param("reviewId") Long reviewId);





}
