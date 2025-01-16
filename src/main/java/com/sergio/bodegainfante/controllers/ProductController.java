package com.sergio.bodegainfante.controllers;

import com.sergio.bodegainfante.dtos.ProductDTO;
import com.sergio.bodegainfante.exceptions.CategoryNotFoundException;
import com.sergio.bodegainfante.exceptions.ProductAlreadyExistsException;
import com.sergio.bodegainfante.exceptions.UnauthorizedAccessException;
import com.sergio.bodegainfante.models.Product;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productName}")
    public ResponseEntity<Product> getProductByName(@PathVariable String productName) {
        Product product= productService.findByName(productName);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para filtrar productos por texto
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> getProductsByTextFilter(@RequestParam String text) {
        List<Product> products = productService.findByTextFilter(text);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint para obtener productos por categoría
    @GetMapping("/category/{id}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long id) {
        List<Product> products = productService.findByCategory(id);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Si no se encuentran productos
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint para crear un producto
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = productService.saveImage(image);
            productDTO.setImage_url(imageUrl);
            String email = userDetails.getUsername();
            Product createdProduct = productService.createProduct(productDTO, email);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (ProductAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Producto ya existe
        } catch (CategoryNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Categoría no encontrada
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // El usuario no tiene acceso
        }
    }

    // Endpoint para actualizar un producto
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Product> updateProduct(@RequestBody ProductDTO productDTO
            ,  @AuthenticationPrincipal UserDetailsImpl userDetails,@RequestParam("image") MultipartFile image) {
        String imageUrl = productService.saveImage(image);
        productDTO.setImage_url(imageUrl);
        String email = userDetails.getUsername();
        Product updatedProduct = productService.updateProduct(productDTO, email);
        if (updatedProduct != null) {
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUsername();
        boolean isDeleted = productService.deleteProduct(id, email);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

