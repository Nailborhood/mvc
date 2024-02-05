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

}
