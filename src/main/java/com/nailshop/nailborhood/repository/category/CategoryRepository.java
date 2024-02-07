package com.nailshop.nailborhood.repository.category;

import com.nailshop.nailborhood.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
