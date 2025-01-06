package com.sergio.bodegainfante.controllers;

import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.repositories.UserRepository;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    UserRepository userRepository;

    // Ruta solo accesible para CUSTOMER: Obtener nombre y apellido de un customer
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> getCustomerNameAndLastName(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            // Obtener el correo electrónico del usuario autenticado
            String email = userDetails.getUsername();

            // Buscar el Customer por su email en la base de datos
            Optional<User> user = userRepository.findByEmail(email);


            if (user.isPresent()) {
                // Si el Customer existe, devolver su nombre completo
                Customer customer = (Customer) user.get();
                String fullName = customer.getFirst_name() + " " + customer.getLast_name();
                return ResponseEntity.ok(fullName);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
    }

}





