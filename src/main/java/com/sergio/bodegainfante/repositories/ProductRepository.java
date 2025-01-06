package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.Category;
import com.sergio.bodegainfante.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Product> findByTextFilter(String text);
    public List<Product> findByCategory(Category category);
    Optional<Product> findByName(String name);
}
