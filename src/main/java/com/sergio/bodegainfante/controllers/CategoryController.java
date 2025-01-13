package com.sergio.bodegainfante.controllers;
import com.sergio.bodegainfante.dtos.CategoryDTO;
import com.sergio.bodegainfante.exceptions.CategoryAlreadyExistsException;
import com.sergio.bodegainfante.exceptions.UnauthorizedAccessException;
import com.sergio.bodegainfante.models.Category;
import com.sergio.bodegainfante.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO categoryDTO, @RequestParam String adminEmail) {
        try {
            Category createdCategory = categoryService.createCategory(categoryDTO, adminEmail);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (CategoryAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // La categoría ya existe
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // El usuario no tiene acceso
        }
    }

    // Endpoint para actualizar una categoría existente
    @PutMapping("/{categoryName}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Category> updateCategory(@PathVariable String categoryName, @RequestBody CategoryDTO categoryDTO, @RequestParam String adminEmail) {
        Category updatedCategory = categoryService.updateCategory(categoryDTO, adminEmail);
        if (updatedCategory != null) {
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no se encuentra la categoría
        }
    }

    // Endpoint para eliminar una categoría
    @DeleteMapping("/{categoryName}")
    @PreAuthorize("hasRole('ADMIN')") // Solo acceso para ADMIN
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryName, @RequestParam String adminEmail) {
        boolean isDeleted = categoryService.deleteCategory(categoryName, adminEmail);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Categoría eliminada correctamente
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Categoría no encontrada
        }
    }
}

