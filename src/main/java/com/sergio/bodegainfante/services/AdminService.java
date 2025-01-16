package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.AdminInfoDTO;
import com.sergio.bodegainfante.dtos.CustomerInfoDTO;
import com.sergio.bodegainfante.models.Admin;
import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.Modification;
import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.models.enums.Role;
import com.sergio.bodegainfante.repositories.ModificationRepository;
import com.sergio.bodegainfante.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService implements IAdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModificationRepository modificationRepository;

    @Override
    public List<Modification> getFilteredModifications(String startDateStr, String endDateStr, Long adminId) {
        LocalDateTime startDate = (startDateStr != null) ? LocalDateTime.parse(startDateStr) : LocalDateTime.MIN;
        LocalDateTime endDate = (endDateStr != null) ? LocalDateTime.parse(endDateStr) : LocalDateTime.MAX;

        return modificationRepository.findFilteredModifications(startDate, endDate, adminId);
    }

    public List<CustomerInfoDTO> getAllCustomers(){
        List<CustomerInfoDTO> customers = new ArrayList<>();
        List<User> users = userRepository.findByRole(Role.CUSTOMER);
        for (User user : users) {
                Customer customer = (Customer) user;
                CustomerInfoDTO customerInfoDTO = new CustomerInfoDTO();
                customerInfoDTO.setId(customer.getUser_id());
                customerInfoDTO.setFirstName(customer.getFirst_name());
                customerInfoDTO.setLastName(customer.getLast_name());
                customerInfoDTO.setEmail(customer.getEmail());
                customerInfoDTO.setPhone(customer.getPhone_number());
                customerInfoDTO.setOrders(customer.getOrders());
                customers.add(customerInfoDTO);
        }
        return customers;
    }

    public List<AdminInfoDTO> getAllAdmins() {
        List<AdminInfoDTO> admins = new ArrayList<>();
        List<User> users = userRepository.findByRole(Role.ADMIN);
        for (User user : users) {
            Admin admin = (Admin) user;
            AdminInfoDTO adminInfoDTO = new AdminInfoDTO();
            adminInfoDTO.setFirstName(admin.getFirst_name());
            adminInfoDTO.setLastName(admin.getLast_name());
            adminInfoDTO.setEmail(admin.getEmail());
            adminInfoDTO.setLast_modification_at(admin.getLast_modification_at());
            adminInfoDTO.setModifications(admin.getModifications());
            admins.add(adminInfoDTO);
        }
        return admins;
    }

    @Transactional
    public boolean updateUserRole(String email, String newRole,String adminEmail) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        Optional<User> adminOpt = userRepository.findByEmail(adminEmail);
        if (userOpt.isPresent() && adminOpt.isPresent()) {
            User user = userOpt.get();
            Admin admin = (Admin) adminOpt.get();
            Role role = Role.fromString(newRole);  // Asegúrate de que el rol sea válido

            if (user.getRole() == role) {
                return false; // Si el rol es el mismo, no actualices
            }

            // Cambiar el rol
            user.setRole(role);
            userRepository.save(user);
            Modification modification = new Modification();
            modification.setAdmin(admin); // Asocia el admin que hizo la modificación
            modification.setDescription("Role changed to " + role.getDisplayName() + " for user " + email);
            modificationRepository.save(modification);

            return true;
        }

        return false; // Si el usuario no existe
    }




}
