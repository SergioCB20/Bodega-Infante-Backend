package com.sergio.bodegainfante.controllers;

import com.sergio.bodegainfante.dtos.PackageDTO;
import com.sergio.bodegainfante.exceptions.*;
import com.sergio.bodegainfante.services.PackageService;
import com.sergio.bodegainfante.models.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    // Obtener todos los paquetes
    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        List<Package> packages = packageService.findAll();
        if (packages.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    // Filtrar paquetes por texto
    @GetMapping("/filter")
    public ResponseEntity<List<Package>> getPackagesByTextFilter(@RequestParam("text") String text) {
        List<Package> packages = packageService.findByTextFilter(text);
        if (packages.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    // Crear un nuevo paquete
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Package> createPackage(@RequestBody PackageDTO packageDTO, @RequestParam String adminEmail) {
        try {
            Package createdPackage = packageService.createPackage(packageDTO, adminEmail);
            return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
        } catch (ProductAlreadyExistsException | UnauthorizedAccessException | CategoryNotFoundException |
                 BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar un paquete existente
    @PutMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Package> updatePackage(@PathVariable Long packageId, @RequestBody PackageDTO packageDTO, @RequestParam String adminEmail) {
        try {
            Package updatedPackage = packageService.updatePackage(packageId,packageDTO, adminEmail);
            return new ResponseEntity<>(updatedPackage, HttpStatus.OK);
        } catch (PackageNotFoundException | UnauthorizedAccessException | ProductNotFoundException | BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar un paquete (eliminación lógica)
    @DeleteMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long packageId, @RequestParam String adminEmail) {
        try {
            boolean isDeleted = packageService.deletePackage(packageId, adminEmail);
            if (isDeleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PackageNotFoundException | UnauthorizedAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

