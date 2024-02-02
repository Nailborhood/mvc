package com.nailshop.nailborhood.repository.shop;

import com.nailshop.nailborhood.domain.shop.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu,Long> {
}
