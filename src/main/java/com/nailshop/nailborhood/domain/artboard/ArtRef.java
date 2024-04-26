package com.nailshop.nailborhood.domain.artboard;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.category.CategoryArt;
import com.nailshop.nailborhood.domain.common.BaseTime;
import com.nailshop.nailborhood.domain.shop.Shop;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "art_ref")
public class ArtRef extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_ref_id")
    private Long artRefId;

    private String name;
    private String content;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "book_mark_count")
    private Long bookMarkCount;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "artRef")
    private List<ArtLike> artLikeList;

    @OneToMany(mappedBy = "artRef")
    private List<ArtImg> artImgList;

    @OneToMany(mappedBy = "artRef")
    private List<CategoryArt> categoryArtList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Builder
    public ArtRef(String name, String content, Long likeCount, Boolean isDeleted, Shop shop, Long bookMarkCount) {
        this.name = name;
        this.content = content;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
        this.shop = shop;
        this.bookMarkCount = bookMarkCount;
    }

    public void updateArtRef(String name, String content){
        this.name = name;
        this.content = content;
    }
}
