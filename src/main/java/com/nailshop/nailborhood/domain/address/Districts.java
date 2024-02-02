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
public class Districts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "districts_id")
    private  Long districtsId;

    private String name;

    @OneToMany(mappedBy = "districts")
    private List<Dong> dongList;

    // 주소(시)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Builder
    public Districts(String name) {
        this.name = name;
    }
}
