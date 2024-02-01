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
    private Long imageId;

    private String url;

    private String s3Name;

    private String isDeleted;

    @Builder
    public ReviewImg(Long imageId, String url, String s3Name, String isDeleted) {
        this.imageId = imageId;
        this.url = url;
        this.s3Name = s3Name;
        this.isDeleted = isDeleted;
    }
}
