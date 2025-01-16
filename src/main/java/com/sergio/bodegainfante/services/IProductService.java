package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.models.Product;
import com.sergio.bodegainfante.dtos.ProductDTO;

import java.util.List;

public interface IProductService {

    List<Product> findAll();

    List<Product> findByTextFilter(String text);

    List<Product> findByCategory(Long categoryId);

    Product createProduct(ProductDTO productDTO, String adminEmail);

    Product updateProduct(ProductDTO productDTO, String adminEmail);

    boolean deleteProduct(Long id, String adminEmail);
}

