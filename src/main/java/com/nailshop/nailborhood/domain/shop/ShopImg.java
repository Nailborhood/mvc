package com.nailshop.nailborhood.domain.shop;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "shop_img")
public class ShopImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_img_id")
    private Long shopImgId;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "img_num")
    private int imgNum;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Builder
    public ShopImg(String imgPath, int imgNum, Boolean isDeleted ,Shop shop) {
        this.imgPath = imgPath;
        this.imgNum = imgNum;
        this.isDeleted = isDeleted;
        this.shop = shop;
    }
}
