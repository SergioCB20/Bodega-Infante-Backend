package com.sergio.bodegainfante.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergio.bodegainfante.dtos.PackageDTO;
import com.sergio.bodegainfante.exceptions.*;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.services.PackageService;
import com.sergio.bodegainfante.models.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> createPackage(
            @RequestPart("data") String rawData,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        try {
            System.out.println("Raw JSON recibido: " + rawData);
            ObjectMapper objectMapper = new ObjectMapper();
            PackageDTO packageDTO = objectMapper.readValue(rawData, PackageDTO.class);
            String adminEmail = userDetails.getUsername();
            Package createdPackage = packageService.createPackage(packageDTO,image, adminEmail);

            return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }





    @PutMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Package> updatePackage(@PathVariable Long packageId, @RequestBody PackageDTO packageDTO
            , @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("image") MultipartFile image) {
        try {
            String adminEmail = userDetails.getUsername();
            Package updatedPackage = packageService.updatePackage(packageId,image,packageDTO, adminEmail);
            return new ResponseEntity<>(updatedPackage, HttpStatus.OK);
        } catch (PackageNotFoundException | UnauthorizedAccessException | ProductNotFoundException | BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long packageId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            String adminEmail = userDetails.getUsername();
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

