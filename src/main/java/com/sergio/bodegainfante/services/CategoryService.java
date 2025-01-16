package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.CategoryDTO;
import com.sergio.bodegainfante.models.*;
import com.sergio.bodegainfante.repositories.CategoryRepository;
import com.sergio.bodegainfante.repositories.ModificationRepository;
import com.sergio.bodegainfante.repositories.ProductRepository;
import com.sergio.bodegainfante.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import com.sergio.bodegainfante.exceptions.CategoryAlreadyExistsException;
import com.sergio.bodegainfante.exceptions.UnauthorizedAccessException;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    @Override
    public Category createCategory(CategoryDTO categoryDTO, String adminEmail) {
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDTO.getName());
        if (optionalCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDTO.getName() + " already exists.");
        }

        // Buscar el admin
        Optional<User> user = userRepository.findByEmail(adminEmail);
        if (user.isEmpty() || !(user.get() instanceof Admin)) {
            throw new UnauthorizedAccessException("User is not an admin or does not exist.");
        }
        Admin admin = (Admin) user.get();

        Category newCategory = new Category();
        newCategory.setName(categoryDTO.getName());
        newCategory.setDescription(categoryDTO.getDescription());
        newCategory.setCreated_at(LocalDateTime.now());
        newCategory.setUpdated_at(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(newCategory);

        Modification modification = new Modification();
        modification.setAdmin(admin);
        modification.setDescription("Category " + categoryDTO.getName() + " created");
        modificationRepository.save(modification);

        return savedCategory;
    }

    @Transactional
    @Override
    public Category updateCategory(CategoryDTO categoryDTO, String adminEmail) {
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDTO.getName());
        if (optionalCategory.isPresent()) {
            Optional<User> user = userRepository.findByEmail(adminEmail);
            if (user.isEmpty() || !(user.get() instanceof Admin)) {
                throw new UnauthorizedAccessException("User is not an admin or does not exist.");
            }
            Admin admin = (Admin) user.get();

            Category category = optionalCategory.get();
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            category.setUpdated_at(LocalDateTime.now());

            Category savedCategory = categoryRepository.save(category);

            Modification modification = new Modification();
            modification.setAdmin(admin);
            modification.setDescription("Category " + categoryDTO.getName() + " updated");
            modificationRepository.save(modification);

            return savedCategory;
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public boolean deleteCategory(Long id, String adminEmail) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Optional<User> user = userRepository.findByEmail(adminEmail);
            if (user.isEmpty() || !(user.get() instanceof Admin)) {
                throw new UnauthorizedAccessException("User is not an admin or does not exist.");
            }
            Admin admin = (Admin) user.get();

            // Obtener los productos relacionados y eliminarlos
            List<Product> products = category.get().getProducts();
            for (Product product : products) {
                product.setCategory(null);
                productRepository.save(product);
            }

            String categoryName = category.get().getName();

            // Eliminación real de la categoría
            categoryRepository.delete(category.get());

            // Crear un registro de modificación
            Modification modification = new Modification();
            modification.setAdmin(admin);
            modification.setDescription("Category " + categoryName + " deleted");
            modificationRepository.save(modification);

            return true;
        }
        return false;
    }

}
