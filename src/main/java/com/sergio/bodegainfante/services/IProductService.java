package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.models.Product;
import com.sergio.bodegainfante.dtos.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {

    List<Product> findAll();

    List<Product> findByTextFilter(String text);

    List<Product> findByCategory(Long categoryId);

    Product createProduct(ProductDTO productDTO, MultipartFile image, String adminEmail);

    Product updateProduct(Long id,ProductDTO productDTO,MultipartFile image, String adminEmail);

    boolean deleteProduct(Long id, String adminEmail);
}

