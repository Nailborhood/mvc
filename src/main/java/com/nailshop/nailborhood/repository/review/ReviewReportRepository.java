package com.nailshop.nailborhood.repository.review;


import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    // 리뷰 신고 처리 상태 변경
    @Query("UPDATE ReviewReport rr " +
            "SET rr.status = :reviewStatus " +
            "WHERE rr.reportId = :reportId")
    @Modifying(clearAutomatically = true)
    void updateReviewStatusByReviewId(Long reportId, String reviewStatus);


    // 신고된 리뷰 조회
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "WHERE r.isDeleted = false AND rr.status =:status")
    Page<ReviewReport> findAllNotDeleted(Pageable pageable, @Param("status") String status);

    // 신고된 리뷰 조회 (삭제여부 상관없이)
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "LEFT JOIN r.reviewImgList ri " +
            "WHERE (r.isDeleted = true AND rr.status =:A AND ri.isDeleted = true ) OR (r.isDeleted = false AND rr.status =:R AND ri.isDeleted = false )")
    Page<ReviewReport> findAllByStatus(Pageable pageable, @Param("A") String accept, @Param("R") String reject);


    // 리뷰 삭제 시 신고된 리뷰 중 해당 reviewId 컬럼 삭제
    @Query("DELETE ReviewReport rr " +
            "WHERE rr.review " +
            "IN " +
            "(SELECT r " +
            "  FROM Review r " +
            "  WHERE r.reviewId = :reviewId)")
    @Modifying(clearAutomatically = true)
    void deleteReviewReportByReviewId(Long reviewId);

    // 리뷰 Id에 해당하는 신고된 리뷰 찾기
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "WHERE r.reviewId = :reviewId AND r.isDeleted = false ")
    ReviewReport findReviewReportByReviewId(@Param("reviewId") Long reviewId);

    // 관리자 리뷰신고 검색 ( 신고자 , 리뷰 작성자 , 매장이름 )
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "LEFT JOIN r.shop s " +
            "WHERE (rr.member.nickname Like %:keyword% OR r.customer.member.nickname like %:keyword% OR s.name Like %:keyword% OR r.contents like %:keyword% ) AND r.isDeleted = false AND rr.status =:status" )
    Page<ReviewReport> findAllReviewReportListBySearch(@Param("keyword")String keyword, Pageable pageable, @Param("status") String status);

    // 관리자 리뷰신고 검색 ( 신고자 , 리뷰 작성자 , 매장이름 )
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "LEFT JOIN r.shop s " +
            "LEFT JOIN r.reviewImgList ri " +
            "WHERE (rr.member.nickname Like %:keyword% OR r.customer.member.nickname like %:keyword% OR s.name Like %:keyword% ) AND ((r.isDeleted = true AND rr.status =:A AND ri.isDeleted = true) OR (r.isDeleted = false AND rr.status =:R AND ri.isDeleted = true))" )
    Page<ReviewReport> findAllReviewReportListBySearchAndStatus(@Param("keyword")String keyword, Pageable pageable,@Param("A") String accept ,@Param("R") String reject);

    // reportId에 해당되는 reviewId 찾기

    @Query("SELECT r.reviewId " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "WHERE rr.reportId =:reportId AND r.isDeleted = false ")
    Long findReviewIdByReportId(@Param("reportId") Long reportId);

    // 리뷰 Id에 해당하는 신고된 리뷰 리스트
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "WHERE r.reviewId = :reviewId AND r.isDeleted = false ")
    List<ReviewReport> findReviewReportListByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "LEFT JOIN r.shop s " +
            "WHERE s.shopId = :shopId AND r.isDeleted = false ")
    List<ReviewReport> findAllByShopId(@Param("shopId") Long shopId);

    // 리뷰 신고 상세 조회
    @Query("SELECT rr " +
            "FROM ReviewReport rr " +
            "LEFT JOIN rr.review r " +
            "WHERE rr.reportId = :reportId AND rr.status =:status AND r.isDeleted = false ")
    ReviewReport findByReportIdAndStatus(@Param("reportId") Long reportId,@Param("status") String status);
}
