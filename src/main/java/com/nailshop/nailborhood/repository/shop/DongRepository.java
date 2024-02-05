package com.nailshop.nailborhood.repository.shop;


import com.nailshop.nailborhood.domain.address.Dong;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface DongRepository extends JpaRepository<Dong, Long> {
   Optional <Dong> findByName(String dongName);
}
