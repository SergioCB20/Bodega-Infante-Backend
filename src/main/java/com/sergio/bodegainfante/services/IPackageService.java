package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.PackageDTO;
import com.sergio.bodegainfante.models.Package;

import java.util.List;

public interface IPackageService {

    // Método para obtener todos los paquetes
    List<Package> findAll();

    List<Package> findByTextFilter(String text);
    // Método para crear un paquete
    Package createPackage(PackageDTO packageDTO, String adminEmail);

    // Método para actualizar un paquete
    Package updatePackage(Long packageId,PackageDTO packageDTO, String adminEmail);

    // Método para eliminar un paquete (lógica de eliminación)
    boolean deletePackage(Long packageId, String adminEmail);
}
