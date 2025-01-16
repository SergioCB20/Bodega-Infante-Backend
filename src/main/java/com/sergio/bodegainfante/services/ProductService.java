package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.ProductDTO;
import com.sergio.bodegainfante.exceptions.CategoryNotFoundException;
import com.sergio.bodegainfante.exceptions.ProductAlreadyExistsException;
import com.sergio.bodegainfante.exceptions.UnauthorizedAccessException;
import com.sergio.bodegainfante.models.*;
import com.sergio.bodegainfante.repositories.CategoryRepository;
import com.sergio.bodegainfante.repositories.ModificationRepository;
import com.sergio.bodegainfante.repositories.ProductRepository;
import com.sergio.bodegainfante.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private UserRepository userRepository;

    private static final String IMAGE_DIR = "src/main/resources/static/images/";

    public String saveImage(MultipartFile image) {
        try {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path path = Paths.get(IMAGE_DIR + fileName);
            Files.write(path, image.getBytes());
            return "/images/" + fileName;  // Esta es la URL relativa de la imagen
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findByTextFilter(String text) {
        return productRepository.findByTextFilter(text);
    }

    public Product findByName(String name) {
        Optional<Product> product = productRepository.findByName(name);
        return product.orElse(null);
    }

    @Override
    public List<Product> findByCategory(Long categoryId) {
        Optional <Category> category = categoryRepository.findById(categoryId);
        return category.map(value -> productRepository.findByCategory(value)).orElse(null);
    }

    @Transactional
    @Override
    public Product createProduct(ProductDTO productDTO, String adminEmail) {
        Optional<Product> optionalProduct = productRepository.findByName(productDTO.getName());
        if (optionalProduct.isPresent()) {
            throw new ProductAlreadyExistsException("Product with name " + productDTO.getName() + " already exists.");
        }

        // Buscar el admin
        Optional<User> user = userRepository.findByEmail(adminEmail);
        if (user.isEmpty() || !(user.get() instanceof Admin)) {
            throw new UnauthorizedAccessException("User is not an admin or does not exist.");
        }
        Admin admin = (Admin) user.get();

        Product newProduct = new Product();
        newProduct.setName(productDTO.getName());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setPrice(productDTO.getPrice());

        Optional<Category> category = categoryRepository.findById(productDTO.getCategoryId());
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("Category not found: " + productDTO.getCategoryId());
        }
        newProduct.setCategory(category.get());
        category.get().getProducts().add(newProduct);
        newProduct.setImage_url(productDTO.getImage_url());
        newProduct.setCreated_at(LocalDateTime.now());
        newProduct.setUpdated_at(LocalDateTime.now());
        categoryRepository.save(category.get());
        Product savedProduct = productRepository.save(newProduct);

        Modification modification = new Modification();
        modification.setAdmin(admin);
        modification.setDescription("Product " + productDTO.getName() + " created");
        modificationRepository.save(modification);

        return savedProduct;
    }

    @Transactional
    @Override
    public Product updateProduct(ProductDTO productDTO, String adminEmail) {
        Optional<Product> optionalProduct = productRepository.findByName(productDTO.getName());
        if (optionalProduct.isPresent()) {
            Optional<User> user = userRepository.findByEmail(adminEmail);
            if (user.isEmpty() || !(user.get() instanceof Admin)) {
                throw new UnauthorizedAccessException("User is not an admin or does not exist.");
            }
            Admin admin = (Admin) user.get();
            Product product = optionalProduct.get();

            // Guardar la categoría anterior antes de cambiarla
            Category oldCategory = product.getCategory();

            // Actualizar el producto con los nuevos datos
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());

            // Verificar si la nueva categoría existe
            Optional<Category> category = categoryRepository.findById(productDTO.getCategoryId());
            if (category.isEmpty()) {
                throw new CategoryNotFoundException("Category not found: " + productDTO.getCategoryId());
            }

            // Si el producto cambió de categoría, eliminarlo de la categoría anterior
            if (!product.getCategory().equals(category.get())) {
                // Eliminar el producto de la lista de productos de la categoría anterior
                oldCategory.getProducts().remove(product);  // Elimina el producto de la lista de la categoría anterior
                categoryRepository.save(oldCategory);  // Guardar la categoría anterior actualizada
            }

            // Asignar la nueva categoría
            product.setCategory(category.get());

            // Establecer otros datos del producto
            product.setImage_url(productDTO.getImage_url());
            product.setUpdated_at(LocalDateTime.now());

            // Guardar el producto actualizado
            Product savedProduct = productRepository.save(product);

            // Registrar la modificación realizada por el admin
            Modification modification = new Modification();
            modification.setAdmin(admin);
            modification.setDescription("Product " + productDTO.getName() + " updated");
            modificationRepository.save(modification);

            // Agregar el producto a la lista de productos de la nueva categoría
            category.get().getProducts().add(savedProduct);  // Agregar el producto a la nueva categoría
            categoryRepository.save(category.get());  // Guardar la nueva categoría

            return savedProduct;
        } else {
            return null;
        }
    }


    @Override
    public boolean deleteProduct(Long id, String adminEmail) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Optional<User> user = userRepository.findByEmail(adminEmail);
            if (user.isEmpty() || !(user.get() instanceof Admin)) {
                throw new UnauthorizedAccessException("User is not an admin or does not exist.");
            }
            Admin admin = (Admin) user.get();
            String productName = product.get().getName();

            productRepository.deleteById(id);
            Modification modification = new Modification();
            modification.setAdmin(admin);
            modification.setDescription("Product " + productName + " deleted");
            modificationRepository.save(modification);
            return true;
        }
        return false;
    }
}

