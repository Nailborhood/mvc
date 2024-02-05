package com.nailshop.nailborhood.repository;

import com.nailshop.nailborhood.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {


    // 리뷰 id로  삭제 안된것만 가져오기
    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.reviewId = :reviewId AND r.isDeleted = false ")
    Optional<Review> findReviewByFalse(@Param("reviewId") Long reviewId);

    // 리뷰 수정
    @Query("UPDATE Review r " +
            "SET r.contents = :contents, " +
            "r.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE r.reviewId = :reviewId")
    @Modifying(clearAutomatically = true)
    void updateReviewContents(@Param("reviewId") Long reviewId, @Param("contents") String contents);


}
