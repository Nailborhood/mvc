package com.nailshop.nailborhood.domain.review;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "review_img")
public class ReviewImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_img_id")
    private Long reviewImgId;

    @Column(name = "img_path")
    private String imgPath;
    @Column(name = "img_num")
    private int imgNum;

    @Column(name = "is_deleted")
    private String isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public ReviewImg(String imgPath, int imgNum, String isDeleted, Review review) {
        this.imgPath = imgPath;
        this.imgNum = imgNum;
        this.isDeleted = isDeleted;
        this.review = review;
    }
}
