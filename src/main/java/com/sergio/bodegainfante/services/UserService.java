package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.AdminInfoDTO;
import com.sergio.bodegainfante.dtos.CustomerInfoDTO;
import com.sergio.bodegainfante.dtos.UserUpdateDTO;
import com.sergio.bodegainfante.models.Admin;
import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.models.enums.Role;
import com.sergio.bodegainfante.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public CustomerInfoDTO getCustomerInfo(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        CustomerInfoDTO customerInfoDTO = new CustomerInfoDTO();
        if (user.isPresent()) {
            Customer customer = (Customer) user.get();
            customerInfoDTO.setFirstName(customer.getFirst_name());
            customerInfoDTO.setLastName(customer.getLast_name());
            customerInfoDTO.setEmail(customer.getEmail());
            customerInfoDTO.setPhone(customer.getPhone_number());
            customerInfoDTO.setOrders(customer.getOrders());
        }
        return customerInfoDTO;
    }

    @Override
    public AdminInfoDTO getAdminInfo(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        AdminInfoDTO adminInfoDTO = new AdminInfoDTO();
        if (user.isPresent()) {
            Admin admin = (Admin) user.get();
            adminInfoDTO.setFirstName(admin.getFirst_name());
            adminInfoDTO.setLastName(admin.getLast_name());
            adminInfoDTO.setEmail(admin.getEmail());
            adminInfoDTO.setLast_modification_at(admin.getLast_modification_at());
            adminInfoDTO.setModifications(admin.getModifications());
        }
        return adminInfoDTO;
    }

    @Override
    public UserUpdateDTO updateUserInfo(String email, UserUpdateDTO userUpdateDTO) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verificar el rol y actualizar los campos correspondientes
            if (user.getRole() == Role.ADMIN) {
                Admin admin = (Admin) user;
                admin.setFirst_name(userUpdateDTO.getFirstName());
                admin.setLast_name(userUpdateDTO.getLastName());
                admin.setEmail(userUpdateDTO.getEmail());
               userRepository.save(admin);
            } else if (user.getRole() == Role.CUSTOMER) {
                Customer customer = (Customer) user;
                customer.setFirst_name(userUpdateDTO.getFirstName());
                customer.setLast_name(userUpdateDTO.getLastName());
                customer.setEmail(userUpdateDTO.getEmail());
                customer.setPhone_number(userUpdateDTO.getPhoneNumber());
               userRepository.save(customer);
            }
            return userUpdateDTO;
        }
        return null;  // Devuelve null si el usuario no existe
    }

}
