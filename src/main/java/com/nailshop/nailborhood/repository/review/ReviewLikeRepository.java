package com.nailshop.nailborhood.repository.review;


import com.nailshop.nailborhood.domain.review.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    // 리뷰 아이디랑 멤버로 좋아요 여부 확인
    @Query("SELECT rl " +
            "FROM ReviewLike rl " +
            "LEFT JOIN rl.member m " +
            "LEFT JOIN rl.review r " +
            "WHERE m.memberId = :memberId AND r.reviewId = :reviewId ")
    ReviewLike findMemberAndReview(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);


    // 좋아요 상태 변경
    @Query("UPDATE ReviewLike rl " +
            "SET rl.status = :status " +
            "WHERE rl.reviewLikeId = :reviewLikeId ")
    @Modifying(clearAutomatically = true)
    void updateStatus(@Param("reviewLikeId") Long reviewLikeId, @Param("status") boolean b);

    // 좋아요 컬럼 삭제
    @Query("UPDATE ReviewLike rl " +
            "SET rl.status = :status " +
            "WHERE rl.review.reviewId = :reviewId ")
    @Modifying(clearAutomatically = true)
    void deleteByReviewId(@Param("reviewId") Long reviewId, @Param("status") boolean b);

}
