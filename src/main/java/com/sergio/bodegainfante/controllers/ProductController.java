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
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.findAll();
        List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping("/{productName}")
    public ResponseEntity<ProductDTO> getProductByName(@PathVariable String productName) {
        Product product= productService.findByName(productName);
        ProductDTO productDTO = new ProductDTO(product);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    // Endpoint para filtrar productos por texto
    @GetMapping("/filter")
    public ResponseEntity<List<ProductDTO>> getProductsByTextFilter(@RequestParam String text) {
        List<Product> products = productService.findByTextFilter(text);
        List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long id) {
        List<Product> products = productService.findByCategory(id);
        List<ProductDTO> productDTOS = products.stream().map(ProductDTO::new).toList();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    // Endpoint para crear un producto
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam("price") double price,
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "image",required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        try {
            ProductDTO productDTO = new ProductDTO(name, description!=null?description:"", price, categoryName);
            Product createdProduct = productService.createProduct(productDTO,image,userDetails.getUsername());
            ProductDTO createdProductDTO = new ProductDTO(createdProduct);
            return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    // Endpoint para actualizar un producto
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam("price") double price,
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "image",required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProductDTO productDTO = new ProductDTO(name, description!=null?description:"", price, categoryName);
        String email = userDetails.getUsername();
        Product updatedProduct = productService.updateProduct(id,productDTO,image, email);
        ProductDTO updatedProductDTO = new ProductDTO(updatedProduct);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

