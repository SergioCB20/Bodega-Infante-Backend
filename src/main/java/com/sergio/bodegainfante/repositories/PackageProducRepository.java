package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.PackageProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageProducRepository extends JpaRepository<PackageProduct,Long> {
}
