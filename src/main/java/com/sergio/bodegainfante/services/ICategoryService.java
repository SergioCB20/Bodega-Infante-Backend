package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.CategoryDTO;
import com.sergio.bodegainfante.models.Category;

import java.util.List;

public interface ICategoryService {

    List<Category> findAll();
    Category createCategory(CategoryDTO categoryDTO, String adminEmail);
    Category updateCategory(CategoryDTO categoryDTO, String adminEmail);
    boolean deleteCategory(Long id, String adminEmail);
}

