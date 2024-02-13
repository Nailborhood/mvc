package com.nailshop.nailborhood.repository.review;

import com.nailshop.nailborhood.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    // shopId에 해당하는 리뷰 리스트
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT join r.shop s " +
            "WHERE s.shopId = :shopId AND s.isDeleted = false AND r.isDeleted = false ")
    List<Review> findAllByShopIdAndIsDeleted(Long shopId);

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

    // 리뷰 전체 조회
    @Query("SELECT r FROM Review r WHERE r.isDeleted = false ")
    Page<Review> findAllIsDeletedFalse(Pageable pageable);

    // 내가 쓴 리뷰 조회
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN c.member m " +
            "WHERE m.memberId = :memberId AND r.isDeleted = false ")
    Page<Review> findMyReviewListByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 리뷰 isdeleted 값 true로
    @Query("UPDATE Review r " +
            "SET r.isDeleted = :status " +
            "WHERE r.reviewId = :reviewId ")
    @Modifying(clearAutomatically = true)
    void deleteReviewId(@Param("reviewId") Long reviewId, @Param("status") Boolean status);

    // 좋아요 개수 증가
    @Query("UPDATE Review r " +
            "SET r.likeCnt = r.likeCnt+1 " +
            "WHERE r.reviewId = :reviewId ")
    @Modifying(clearAutomatically = true)
    void likeCntIncrease(@Param("reviewId") Long reviewId);


    // 좋아요 개수 감소
    @Query("UPDATE Review r " +
            "SET r.likeCnt = r.likeCnt-1 " +
            "WHERE r.reviewId = :reviewId ")
    @Modifying(clearAutomatically = true)
    void likeCntDecrease(@Param("reviewId") Long reviewId);


    // 리뷰 삭제시 좋아요 갯수 0으로
    @Query("UPDATE Review r " +
            "SET r.likeCnt = 0 " +
            "WHERE r.reviewId = :reviewId ")
    @Modifying(clearAutomatically = true)
    void likeCntZero(@Param("reviewId") Long reviewId);

    // 매장에 해당 하는 리뷰 리스트 불러오기
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT join r.shop s " +
            "WHERE s.shopId = :shopId AND s.isDeleted = false ")
    Page<Review> findAllNotDeletedBYShopId(Pageable pageable, @Param("shopId") Long shopId);

    @Query("UPDATE Review r " +
            "SET r.isDeleted = true, " +
            "r.rate = 0" +
            "where r.reviewId = :reviewId")
    @Modifying(clearAutomatically = true)
    void reviewDeleteByReviewId(@Param("reviewId") Long reviewId);

    // 리뷰 검색 ( 매장이름, 내용 )
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "WHERE (r.contents Like %:keyword% OR s.name Like %:keyword% ) AND r.isDeleted = false " )
    Page<Review> findReviewListBySearch(@Param("keyword")String keyword, Pageable pageable);

}
