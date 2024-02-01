package com.nailshop.nailborhood.domain.review;

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
public class ReviewImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewImgId;

    private String imgPath;
    private int imgNum;

    private String isDeleted;

    @Builder
    public ReviewImg(String imgPath, int imgNum, String isDeleted) {
        this.imgPath = imgPath;
        this.imgNum = imgNum;
        this.isDeleted = isDeleted;
    }
}
