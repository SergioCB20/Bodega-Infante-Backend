package com.sergio.bodegainfante.security;
import com.sergio.bodegainfante.models.Admin;
import com.sergio.bodegainfante.models.enums.Role;
import com.sergio.bodegainfante.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;  // Acceso al repositorio de usuarios

    @Autowired
    private PasswordEncoder passwordEncoder;  // Inyección del PasswordEncoder

    @PostConstruct
    public void init() {
        // Verifica si ya existe un usuario con el email admin@example.com
        if (!userRepository.existsByEmail("sergio@example.com")) {
            Admin admin = new Admin();
            admin.setFirst_name("Sergio");
            admin.setLast_name("Chumbimuni");
            admin.setEmail("sergio@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));  // Encriptar la contraseña antes de guardarla
            admin.setRole(Role.ADMIN);  // Asignar el rol de ADMIN
            admin.setCreated_at(LocalDateTime.now());
            admin.setUpdated_at(LocalDateTime.now());

            userRepository.save(admin);  // Guardar el nuevo admin en la base de datos
            System.out.println("Admin default created!");
        }
    }
}

