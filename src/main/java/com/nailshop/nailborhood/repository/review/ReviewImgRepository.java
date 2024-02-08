package com.nailshop.nailborhood.repository.review;

import com.nailshop.nailborhood.domain.review.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {

    // 리뷰 ID로 리뷰 IMG 삭제
    @Query("SELECT ri " +
            "FROM ReviewImg ri " +
            "LEFT JOIN ri.review r " +
            "WHERE r.reviewId = :reviewId")
    List<ReviewImg> findByReviewImgListReviewId(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("DELETE FROM ReviewImg ri " +
            "WHERE ri.review.reviewId = :reviewId ")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    // 리뷰 이미지 삭제하기 위한 이미지 리스트
    @Query("SELECT ri " +
            "FROM ReviewImg  ri " +
            "WHERE ri.review.reviewId = :reviewId ")
    List<ReviewImg> findDeleteReviewImgPathList(@Param("reviewId") Long reviewId);

    // 리뷰 이미지 isdeleted true로 변경
    @Query("UPDATE ReviewImg ri " +
            "SET ri.isDeleted = :status " +
            "WHERE ri.reviewImgId = :reviewImgId ")
    @Modifying(clearAutomatically = true)
    void deleteByReviewImgId(@Param("reviewImgId") Long reviewImgId, @Param("status") Boolean status);


    // 매장에 해당 하는 리뷰 대표 이미지 불러오기
    @Query("SELECT ri.imgPath " +
            "FROM ReviewImg ri " +
            "LEFT JOIN ri.review r " +
            "LEFT JOIN r.shop s " +
            "WHERE r.reviewId = :reviewId AND s.shopId = :shopId AND ri.imgNum = 1")
    String findReviewImgByShopIdAndReviewId(@Param("shopId") Long shopId, @Param("reviewId") Long reviewId);


}
