package com.nailshop.nailborhood.repository.address;


import com.nailshop.nailborhood.domain.address.Dong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface DongRepository extends JpaRepository<Dong, Long> {
   // 주소(동) 확인
   Optional <Dong> findByName(String dongName);


    @Query("SELECT d " +
            "FROM Dong d " +
            "WHERE d.dongId =:dongId")
    Dong findByDongId(@Param("dongId") Long dongId);


    Dong findAllByDongId(Long dongId);
}
