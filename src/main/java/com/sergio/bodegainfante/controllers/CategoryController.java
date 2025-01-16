package com.sergio.bodegainfante.controllers;
import com.sergio.bodegainfante.dtos.CategoryDTO;
import com.sergio.bodegainfante.exceptions.CategoryAlreadyExistsException;
import com.sergio.bodegainfante.exceptions.UnauthorizedAccessException;
import com.sergio.bodegainfante.models.Category;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Endpoint para obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categories = categoryService.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Endpoint para crear una nueva categoría
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO categoryDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            String email = userDetails.getUsername();
            Category createdCategory = categoryService.createCategory(categoryDTO, email);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (CategoryAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // La categoría ya existe
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // El usuario no tiene acceso
        }
    }

    // Endpoint para actualizar una categoría existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Category> updateCategory(@RequestBody CategoryDTO categoryDTO,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUsername();
        Category updatedCategory = categoryService.updateCategory(categoryDTO, email);
        if (updatedCategory != null) {
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no se encuentra la categoría
        }
    }

    // Endpoint para eliminar una categoría
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUsername();
        boolean isDeleted = categoryService.deleteCategory(id, email);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

