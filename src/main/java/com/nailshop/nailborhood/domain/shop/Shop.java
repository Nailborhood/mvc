package com.nailshop.nailborhood.domain.shop;

import com.nailshop.nailborhood.domain.address.Dong;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.common.BaseTime;
import com.nailshop.nailborhood.domain.member.Favorite;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.type.ShopStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Shop extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    private String name;
    private String address;
    private String opentime;
    private String website;
    private String content;
    private String phone;

    @Enumerated(EnumType.STRING)
    private ShopStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    // 메뉴
    @OneToMany(mappedBy = "shop")
    private List<Menu> menuList;

    // 매장이미지
    @OneToMany(mappedBy = "shop")
    private List<ShopImg> shopImgList;

    // 매장 저장
    @OneToMany(mappedBy = "shop")
    private List<Favorite> favoriteList;

    // 아트판
    @OneToMany(mappedBy = "shop")
    private List<ArtRef> artRefList;

    // 리뷰
    @OneToMany(mappedBy = "shop")
    private List<Review> reviewList;


    // 주소(동)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_id")
    private Dong dong;



    //private double latitude; // 위도
    //private double longitude; // 경도



    @Builder
    public Shop(String name, String address, String opentime, String website, String content, ShopStatus status, Boolean isDeleted, Dong dong ,String phone) {
        this.name = name;
        this.address = address;
        this.opentime = opentime;
        this.website = website;
        this.content = content;
        this.status = status;
        this.isDeleted = isDeleted;
        this.dong = dong;
        this.phone = phone;
    }

    public  void shopUpdate (String name, String address, String opentime, String website, String content, ShopStatus status, Dong dong ,String phone) {
        this.name = name;
        this.address = address;
        this.opentime = opentime;
        this.website = website;
        this.content = content;
        this.status = status;
        this.dong = dong;
        this.phone = phone;
    }



}
