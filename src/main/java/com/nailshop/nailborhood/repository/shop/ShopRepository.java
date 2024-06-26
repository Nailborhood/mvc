package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.type.ShopStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    // 매장 ShopId로 존재 여부 확인 (isDeleted = false)
    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE s.shopId =:shopId AND s.isDeleted = false ")
    Optional<Shop> findByShopIdAndIsDeleted(@Param("shopId") Long shopId);

    // 매장 리스트 page 처리
    @Query("SELECT s FROM Shop s WHERE s.isDeleted = false AND s.status = 'OPEN'")
    Page<Shop> findAllNotDeleted(Pageable pageable);

    // 주소(동) dongId에 해당되는 매장 리스트
    @Query("SELECT s " +
            "FROM Shop s " +
            "left JOIN s.dong d " +
            "WHERE s.isDeleted = false AND d.dongId =:dongId AND s.status = 'OPEN'")
    Page<Shop> findAllNotDeletedByDongId(Pageable pageable, @Param("dongId") Long dongId);

    // 매장 별점 (리뷰 등록 시 rateAvg update)
    @Query("UPDATE Shop s " +
            "SET s.rateAvg = :rateAvg " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateRateAvgByShopId(@Param("rateAvg") double rateAvg, @Param("shopId") Long shopId);

    // 매장 찜 증가
    @Query("UPDATE Shop s " +
            "SET s.favoriteCnt = s.favoriteCnt + 1 " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateFavoriteCntIncreaseByShopId(@Param("shopId") Long shopId);

    // 매장 찜 감소
    @Query("UPDATE Shop s " +
            "SET s.favoriteCnt = s.favoriteCnt - 1 " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateFavoriteCntDecreaseByShopId(@Param("shopId") Long shopId);

    // 매장 삭제
    @Query("UPDATE Shop s " +
            "SET s.isDeleted = true," +
            "s.status = :shopStatus " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void shopDeleteByShopId(@Param("shopId") Long shopId ,@Param("shopStatus") ShopStatus shopStatus);

    // 매장 상태 변경
    @Query("UPDATE Shop s " +
            "SET s.status = :status " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateShopStatusByShopId(@Param("shopId") Long shopId, @Param("status") ShopStatus changeStatus);

    // 매장 검색
    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE s.name Like %:keyword%  AND s.isDeleted = false " )
    Page<Shop> findShopListByKeyword(@Param("keyword") String keyword, PageRequest pageable);

    // 관리자 매장 검색
    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE s.name Like %:keyword% AND s.status <> 'READY'" )
    Page<Shop> findALlShopListByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 신청 매장 전체 조회
    Page<Shop> findByIsDeletedFalseAndStatus(ShopStatus status, Pageable pageable);


    // 리뷰 개수 증가
    @Query("UPDATE Shop s " +
            "SET s.reviewCnt = s.reviewCnt + 1 " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateReviewCntIncreaseByShopId(@Param("shopId") Long shopId);

    // 리뷰 개수 감소
    @Query("UPDATE Shop s " +
            "SET s.reviewCnt = s.reviewCnt - 1 " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateReviewCntDecreaseByShopId(@Param("shopId") Long shopId);

    @Query("SELECT s FROM Shop s WHERE s.isDeleted = false and s.status = 'READY'")
    Page<Shop> findAllNotDeletedAndReady(Pageable pageable);

    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE (s.name Like %:keyword% ) and s.status = 'READY'" )
    Page<Shop> findAllNotDeletedAndReadyBySearch(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.owner o " +
            "WHERE o.ownerId = :ownerId")
    Shop findAllShopListByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.owner o " +
            "WHERE o.ownerId = :ownerId")
    Optional<Shop> findAllShopByOwnerId(@Param("ownerId") Long ownerId);

    @Query("UPDATE Shop s " +
            "SET s.dong.dongId = :dongId " +
            "WHERE s.shopId = :shopId")
    @Modifying(clearAutomatically = true)
    void updateDongIdByShopId(@Param("dongId") Long dongId, @Param("shopId") Long shopId);

    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE s.shopId =:shopId ")
    Optional<Shop> findByShopId(@Param("shopId") Long shopId);

    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.dong d " +
            "WHERE (s.name Like %:keyword% ) AND s.isDeleted = false AND d.dongId =:dongId AND s.status = 'OPEN'")
    Page<Shop> findAllNotDeletedByDongIdAndKeyword(Pageable pageable, @Param("dongId") Long dongId,@Param("keyword") String keyword);

    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.dong.districts.city c " +
            "WHERE s.isDeleted =false AND c.cityId =:cityId AND s.status = 'OPEN' ")
    Page<Shop> findAllNotDeletedByCityId(Pageable pageable, @Param("cityId") Long cityId);
    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.dong.districts dt " +
            "WHERE s.isDeleted =false AND dt.districtsId =:districtsId AND s.status = 'OPEN'")
    Page<Shop> findAllNotDeletedByDistrictsId(Pageable pageable, @Param("districtsId") Long districtsId);

    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.dong.districts.city c " +
            "WHERE (s.name Like %:keyword% ) AND s.isDeleted = false AND c.cityId =:cityId AND s.status = 'OPEN'")
    Page<Shop> findAllNotDeletedByCityIdAndKeyword(Pageable pageable, @Param("cityId") Long cityId,@Param("keyword") String keyword);

    @Query("SELECT s " +
            "FROM Shop s " +
            "LEFT JOIN s.dong.districts dt " +
            "WHERE (s.name Like %:keyword% ) AND s.isDeleted = false AND dt.districtsId =:districtsId AND s.status = 'OPEN'")
    Page<Shop> findAllNotDeletedByDistrictsIdAndKeyword(Pageable pageable, @Param("districtsId") Long districtsId, @Param("keyword") String keyword);

    // 관리자 매장 조회
    @Query("SELECT s " +
            "FROM Shop s " +
            "WHERE s.status <> 'READY'" )
    Page<Shop> findAllShopListByStatus(PageRequest pageable);
}
