package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String categoryName);
}
