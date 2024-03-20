package com.nailshop.nailborhood.repository.address;


import com.nailshop.nailborhood.domain.address.Dong;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface DongRepository extends JpaRepository<Dong, Long> {
   // 주소(동) 확인
   Optional <Dong> findByName(String dongName);
}
