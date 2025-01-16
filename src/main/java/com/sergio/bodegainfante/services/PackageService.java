package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.PackageDTO;
import com.sergio.bodegainfante.dtos.PackageProductDTO;
import com.sergio.bodegainfante.exceptions.*;
import com.sergio.bodegainfante.models.*;
import com.sergio.bodegainfante.models.Package;
import com.sergio.bodegainfante.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PackageService implements IPackageService {

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

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
    public List<Package> findAll() {
        return packageRepository.findAll();
    }

    @Override
    public List<Package> findByTextFilter(String text) {
        return packageRepository.findByTextFilter(text);
    }

    @Transactional
    @Override
    public Package createPackage(PackageDTO packageDTO, String adminEmail) {
        Optional<Package> optionalPackage = packageRepository.findByName(packageDTO.getName());
        if (optionalPackage.isPresent()) {
            throw new PackageAlreadyExistsException("Package with name " + packageDTO.getName() + " already exists.");
        }

        // Buscar el admin
        Optional<User> user = userRepository.findByEmail(adminEmail);
        if (user.isEmpty() || !(user.get() instanceof Admin)) {
            throw new UnauthorizedAccessException("User is not an admin or does not exist.");
        }
        Admin admin = (Admin) user.get();

        // Crear el nuevo paquete
        Package newPackage = new Package();
        newPackage.setName(packageDTO.getName());
        newPackage.setDescription(packageDTO.getDescription());
        newPackage.setPrice(packageDTO.getPrice());
        newPackage.setImage_url(packageDTO.getImage_url());
        newPackage.setAvailable(packageDTO.isAvailable());

        // Lista de productos asociados al paquete
        List<PackageProduct> packageProductList = new ArrayList<>();
        for (PackageProductDTO productDTO : packageDTO.getProducts()) {
            Optional<Product> product = productRepository.findById(productDTO.getProductId());
            if (product.isPresent()) {
                PackageProduct packageProduct = new PackageProduct();
                packageProduct.setPkg(newPackage);
                packageProduct.setQuantity(productDTO.getQuantity());
                packageProduct.setProduct(product.get());
                packageProductList.add(packageProduct);
            } else {
                // Si algún producto no se encuentra, lanzamos una excepción
                throw new ProductNotFoundException("Product with ID " + productDTO.getProductId() + " not found.");
            }
        }

        // Si no se agregan productos al paquete, lanzamos una excepción
        if (packageProductList.isEmpty()) {
            throw new BadRequestException("No products selected for the package.");
        }

        // Asignar los productos al paquete
        newPackage.setPackageProducts(packageProductList);
        newPackage.setCreated_at(LocalDateTime.now());
        newPackage.setUpdated_at(LocalDateTime.now());

        // Guardar el paquete en la base de datos
        Package savedPackage = packageRepository.save(newPackage);

        // Registrar la modificación
        Modification modification = new Modification();
        modification.setAdmin(admin);
        modification.setDescription("Package " + packageDTO.getName() + " created");
        modificationRepository.save(modification);

        return savedPackage;
    }


    @Transactional
    @Override
    public Package updatePackage(Long packageId,PackageDTO packageDTO, String adminEmail) {
        // Verificar si el paquete existe
        Optional<Package> optionalPackage = packageRepository.findById(packageId);
        if (optionalPackage.isEmpty()) {
            throw new PackageNotFoundException("Package "  + " not found.");
        }

        // Buscar el admin
        Optional<User> user = userRepository.findByEmail(adminEmail);
        if (user.isEmpty() || !(user.get() instanceof Admin)) {
            throw new UnauthorizedAccessException("User is not an admin or does not exist.");
        }
        Admin admin = (Admin) user.get();

        // Actualizar el paquete
        Package existingPackage = optionalPackage.get();
        existingPackage.setName(packageDTO.getName());
        existingPackage.setDescription(packageDTO.getDescription());
        existingPackage.setPrice(packageDTO.getPrice());
        existingPackage.setImage_url(packageDTO.getImage_url());
        existingPackage.setAvailable(packageDTO.isAvailable());

        // Actualizar los productos del paquete
        List<PackageProduct> packageProductList = new ArrayList<>();
        for (PackageProductDTO productDTO : packageDTO.getProducts()) {
            Optional<Product> product = productRepository.findById(productDTO.getProductId());
            if (product.isPresent()) {
                PackageProduct packageProduct = new PackageProduct();
                packageProduct.setPkg(existingPackage);
                packageProduct.setQuantity(productDTO.getQuantity());
                packageProduct.setProduct(product.get());
                packageProductList.add(packageProduct);
            } else {
                throw new ProductNotFoundException("Product with ID " + productDTO.getProductId() + " not found.");
            }
        }

        if (packageProductList.isEmpty()) {
            throw new BadRequestException("No products selected for the package.");
        }

        existingPackage.setPackageProducts(packageProductList);
        existingPackage.setUpdated_at(LocalDateTime.now());

        // Guardar el paquete actualizado
        Package savedPackage = packageRepository.save(existingPackage);

        // Registrar la modificación
        Modification modification = new Modification();
        modification.setAdmin(admin);
        modification.setDescription("Package " + packageDTO.getName() + " updated");
        modificationRepository.save(modification);

        return savedPackage;
    }


    @Transactional
    @Override
    public boolean deletePackage(Long packageId, String adminEmail) {
        // Verificar si el paquete existe
        Optional<Package> packageOptional = packageRepository.findById(packageId);
        if (packageOptional.isEmpty()) {
            throw new PackageNotFoundException("Package with ID " + packageId + " not found.");
        }

        // Buscar el admin
        Optional<User> user = userRepository.findByEmail(adminEmail);
        if (user.isEmpty() || !(user.get() instanceof Admin)) {
            throw new UnauthorizedAccessException("User is not an admin or does not exist.");
        }
        Admin admin = (Admin) user.get();

        Package packageToDelete = packageOptional.get();
        String packageName = packageToDelete.getName();
        packageRepository.delete(packageToDelete);

        // Registrar la modificación
        Modification modification = new Modification();
        modification.setAdmin(admin);
        modification.setDescription("Package " + packageName + " deleted");
        modificationRepository.save(modification);

        return true;
    }

}

