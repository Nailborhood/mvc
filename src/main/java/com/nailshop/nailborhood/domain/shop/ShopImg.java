package com.nailshop.nailborhood.domain.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ShopImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String imgPath;
    private int imgNum;
    private Boolean isDeleted;

    @Builder
    public ShopImg(String imgPath, int imgNum, Boolean isDeleted) {
        this.imgPath = imgPath;
        this.imgNum = imgNum;
        this.isDeleted = isDeleted;
    }
}
