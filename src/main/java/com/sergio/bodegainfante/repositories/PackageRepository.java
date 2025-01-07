package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {
    @Query("SELECT p FROM Package p WHERE p.name LIKE %:text% OR p.description LIKE %:text%")
    List<Package> findByTextFilter(@Param("text") String text);
    Optional<Package> findByName(String name);

}
