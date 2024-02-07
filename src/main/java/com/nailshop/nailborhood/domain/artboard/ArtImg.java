package com.nailshop.nailborhood.domain.artboard;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "art_img")
public class ArtImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_img_id")
    private  Long artImgId;

    @Column(name = "img_path")
    private String imgPath;
    @Column(name = "img_num")
    private int imgNum;
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref_id")
    private ArtRef artRef;

    @Builder
    public ArtImg(String imgPath, int imgNum, Boolean isDeleted, ArtRef artRef) {
        this.imgPath = imgPath;
        this.imgNum = imgNum;
        this.isDeleted = isDeleted;
        this.artRef = artRef;
    }
}
