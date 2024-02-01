package com.nailshop.nailborhood.domain.shop;

import com.nailshop.nailborhood.domain.common.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@Getter
@NoArgsConstructor
@Entity
public class Shop extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String openTime;
    private String website;
    private String content;
    private String status;
    private Boolean isDeleted;
    //private double latitude; // 위도
    //private double longitude; // 경도


    @Builder
    public Shop(String name, String address, String openTime, String website, String content, String status, Boolean isDeleted) {
        this.name = name;
        this.address = address;
        this.openTime = openTime;
        this.website = website;
        this.content = content;
        this.status = status;
        this.isDeleted = isDeleted;
        //this.latitude = latitude;
        //this.longitude = longitude;
    }
}
