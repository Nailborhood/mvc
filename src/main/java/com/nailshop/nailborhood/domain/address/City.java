package com.nailshop.nailborhood.domain.address;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private  Long cityId;

    private String name;

    @OneToMany(mappedBy = "city")
    private List<Districts> districtsList;

    @Builder
    public City(String name) {
        this.name = name;
    }
}
