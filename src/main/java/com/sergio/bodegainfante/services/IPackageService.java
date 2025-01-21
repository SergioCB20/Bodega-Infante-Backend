package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.PackageDTO;
import com.sergio.bodegainfante.models.Package;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPackageService {

    // Método para obtener todos los paquetes
    List<Package> findAll();

    List<Package> findByTextFilter(String text);
    // Método para crear un paquete
    Package createPackage(PackageDTO packageDTO, MultipartFile image, String adminEmail);

    // Método para actualizar un paquete
    Package updatePackage(Long packageId,MultipartFile image, PackageDTO packageDTO, String adminEmail);

    // Método para eliminar un paquete (lógica de eliminación)
    boolean deletePackage(Long packageId, String adminEmail);
}
