package com.nailshop.nailborhood.repository.address;

import com.nailshop.nailborhood.domain.address.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
