package com.nailshop.nailborhood.domain.address;

import com.nailshop.nailborhood.domain.shop.Shop;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Dong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dong_id")
    private  Long dongId;

    private String name;

    @OneToMany(mappedBy = "dong")
    private List<Shop> shopList;

    // 주소(구)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "districts_id")
    private Districts districts;



    @Builder
    public Dong(String name) {
        this.name = name;
    }
}
