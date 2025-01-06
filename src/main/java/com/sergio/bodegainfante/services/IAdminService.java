package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.AdminInfoDTO;
import com.sergio.bodegainfante.dtos.CustomerInfoDTO;
import com.sergio.bodegainfante.models.Modification;

import java.util.List;

public interface IAdminService {
    public List<CustomerInfoDTO> getAllCustomers();
    public List<AdminInfoDTO> getAllAdmins();
    public boolean updateUserRole(String email, String newRole, String adminEmail);
    public List<Modification> getFilteredModifications(String startDateStr, String endDateStr, Long adminId);
}
