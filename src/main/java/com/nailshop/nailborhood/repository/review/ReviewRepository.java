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

    // 리뷰 id로 삭제 안된것만 가져오기
    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.reviewId = :reviewId AND r.isDeleted = false ")
    Optional<Review> findReviewByFalse(@Param("reviewId") Long reviewId);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.reviewId = :reviewId ")
    Optional<Review> findDetailReview(@Param("reviewId") Long reviewId);



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

    // 카테고리 선택 조회 , 검색어 X
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.categoryReviewList cr " +
            "WHERE r.isDeleted = false AND cr.category.categoryId IN :categoryIdList GROUP BY r " +
            "HAVING COUNT(DISTINCT cr.category) = :numberOfSelectedCategories ")
    Page<Review> findByCategoryIdListAndIsDeletedFalse(List<Long> categoryIdList, int numberOfSelectedCategories ,Pageable pageable);

    // 내가 쓴 리뷰 조회
    // 리뷰 true, false 다 불러옴
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN c.member m " +
            "LEFT JOIN r.reviewReportList rr " +
            "WHERE m.memberId = :memberId ")
//            "WHERE m.memberId = :memberId AND r.isDeleted = false OR (r.isDeleted = true AND rr.status = :status)")
//    Page<Review> findMyReviewListByMemberId(@Param("memberId") Long memberId, Pageable pageable, @Param("status") String status);
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
    // 카테고리X, 검색어 X
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT join r.shop s " +
            "WHERE s.shopId = :shopId AND r.isDeleted = false ")
    Page<Review> findAllNotDeletedByShopId(Pageable pageable, @Param("shopId") Long shopId);

    // 매장에 해당 하는 리뷰 리스트 불러오기
    // 카테고리 O, 검색어 X
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "LEFT JOIN r.categoryReviewList cr " +
            "WHERE s.shopId = :shopId AND r.isDeleted = false AND cr.category.categoryId IN :categoryIdList GROUP BY r " +
            "HAVING COUNT(DISTINCT cr.category) = :numberOfSelectedCategories ")
    Page<Review> findAllByCategoryIdListNotDeletedByShopId(List<Long> categoryIdList, int numberOfSelectedCategories, Pageable pageable, @Param("shopId") Long shopId);

    // 매장 리뷰 검색 ( 매장이름, 내용 ) 검색어 O, 카테고리 X
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "WHERE (r.contents LIKE %:keyword% OR s.name LIKE %:keyword%) AND r.isDeleted = false AND s.shopId = :shopId " )
    Page<Review> findShopReviewListBySearch(@Param("keyword")String keyword, Pageable pageable, @Param("shopId") Long shopId);

    // 매장 리뷰 검색 (매장이름, 내용) 검색어 O, 카테고리 O )
    @Query("SELECT r FROM Review r " +
            "LEFT JOIN r.shop s " +
            "JOIN r.categoryReviewList cr " +
            "WHERE (r.contents LIKE %:keyword% OR s.name LIKE %:keyword%) " +
            "AND r.isDeleted = false " +
            "AND s.shopId = :shopId " +
            "AND cr.category.categoryId IN :categoryIdList " +
            "GROUP BY r " +
            "HAVING COUNT(DISTINCT cr.category) = :numberOfSelectedCategories")
    Page<Review> findShopReviewByKeywordAndCategories(@Param("keyword")String keyword, @Param("categoryIdList") List<Long> categoryIdList, @Param("numberOfSelectedCategories") int numberOfSelectedCategories, Pageable pageable, @Param("shopId") Long shopId);

    @Query("UPDATE Review r " +
            "SET r.isDeleted = true, " +
            "r.rate = 0" +
            "where r.reviewId = :reviewId")
    @Modifying(clearAutomatically = true)
    void reviewDeleteByReviewId(@Param("reviewId") Long reviewId);

    // 리뷰 검색 ( 매장이름, 내용 ) 검색어, 카테고리 O
    @Query("SELECT r FROM Review r " +
            "LEFT JOIN r.shop s " +
            "JOIN r.categoryReviewList cr " +
            "WHERE (r.contents LIKE %:keyword% OR s.name LIKE %:keyword%) " +
            "AND r.isDeleted = false " +
            "AND cr.category.categoryId IN :categoryIdList " +
            "GROUP BY r " +
            "HAVING COUNT(DISTINCT cr.category) = :numberOfSelectedCategories")
    Page<Review> findReviewByKeywordAndCategories(@Param("keyword")String keyword, @Param("categoryIdList") List<Long> categoryIdList, @Param("numberOfSelectedCategories") int numberOfSelectedCategories, Pageable pageable);

    // 리뷰 검색 ( 매장이름, 내용 ) 검색어O, 카테고리 X
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "WHERE (r.contents LIKE %:keyword% OR s.name LIKE %:keyword%) AND r.isDeleted = false " )
    Page<Review> findReviewListBySearch(@Param("keyword")String keyword, Pageable pageable);



    // 사업자 리뷰 검색 (리뷰 내용, 작성자) 검색어 있을 때
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN c.member m " +
            "LEFT JOIN r.reviewReportList rr " +
            "WHERE (s.shopId = :shopId AND (r.contents Like %:keyword% OR m.nickname LIKE  %:keyword% ) AND r.isDeleted = false) " )
    Page<Review> findAllReviewListBySearchOwner(Pageable pageable, @Param("shopId") Long shopId, @Param("keyword")String keyword);

    // 사업자 리뷰 검색 (리뷰 내용, 작성자) 검색어 없을 때
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN c.member m " +
            "LEFT JOIN r.reviewReportList rr " +
            "WHERE s.shopId = :shopId AND r.isDeleted = false " )
    Page<Review> findAllReviewListByOwner(Pageable pageable, @Param("shopId") Long shopId);

    // 관리자 리뷰 검색 ( 매장이름, 내용 )
    @Query("SELECT r " +
            "FROM Review r " +
            "LEFT JOIN r.shop s " +
            "WHERE (r.contents Like %:keyword% OR r.customer.member.nickname like %:keyword% OR s.name Like %:keyword% )" )
    Page<Review> findAllReviewListBySearch(@Param("keyword")String keyword, Pageable pageable);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.reviewId = :reviewId AND r.isDeleted = false ")
    Review findByReviewId(Long reviewId);
}
