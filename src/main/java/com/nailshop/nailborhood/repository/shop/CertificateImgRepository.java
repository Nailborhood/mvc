package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.CertificateImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CertificateImgRepository extends JpaRepository<CertificateImg,Long> {


    @Query("SELECT ci " +
            "FROM CertificateImg ci " +
            "LEFT JOIN ci.shop s " +
            "WHERE s.shopId =:shopId ")
    CertificateImg findByShopId(@Param("shopId") Long shopId);


    @Query("UPDATE CertificateImg  ci " +
            "SET ci.isDeleted = :status " +
            "WHERE ci.certificateImgId = :certificateImgId ")
    @Modifying(clearAutomatically = true)
    void deleteByCertificateImgId(@Param("certificateImgId") Long certificateImgId, @Param("status") boolean status);
}
