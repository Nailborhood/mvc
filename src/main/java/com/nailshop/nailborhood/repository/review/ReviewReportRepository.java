package com.nailshop.nailborhood.repository.review;


import com.nailshop.nailborhood.domain.review.ReviewReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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
            "WHERE r.isDeleted = false")
    Page<ReviewReport> findAllNotDeleted(Pageable pageable);

    // 리뷰 삭제 시 신고된 리뷰 중 해당 reviewId 컬럼 삭제
    @Query("DELETE ReviewReport rr " +
            "WHERE rr.review " +
            "IN " +
            "(SELECT r " +
            "  FROM Review r " +
            "  WHERE r.reviewId = :reviewId)")
    @Modifying(clearAutomatically = true)
    void deleteReviewReportByReviewId(Long reviewId);
}
