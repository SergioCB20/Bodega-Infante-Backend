package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.LoginRequestDTO;
import com.sergio.bodegainfante.dtos.RegistrationRequestDTO;
import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.models.enums.Role;
import com.sergio.bodegainfante.repositories.UserRepository;
import com.sergio.bodegainfante.security.IAuthService;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.security.UserDetailsServiceImpl;
import com.sergio.bodegainfante.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public HashMap<String, String> register(RegistrationRequestDTO registrationRequest) {
        HashMap<String, String> response = new HashMap<>();

        if(!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            response.put("error", "Passwords do not match");
            return response;
        }

        // Verificar si el email ya está en uso
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            response.put("error", "Email already in use");
            return response;
        }

        // Crear y guardar el nuevo usuario
        Customer user = new Customer();
        user.setFirst_name(registrationRequest.getFirstName());
        user.setLast_name(registrationRequest.getLastName());
        user.setRole(Role.CUSTOMER); // Establecer el rol por defecto
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Encriptar la contraseña

        userRepository.save(user);

        response.put("message", "User registered successfully");
        return response;
    }

    @Override
    public HashMap<String, String> login(LoginRequestDTO loginRequest) {
        HashMap<String, String> response = new HashMap<>();

        try {
            // Autenticación del usuario con el email y la contraseña
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Si la autenticación es exitosa, generar el JWT
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar el token JWT con el email (username)
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Devolver el JWT en la respuesta
            response.put("token", jwt);
            response.put("message", "Login successful");

        } catch (BadCredentialsException e) {
            response.put("error", e.getMessage());
        }

        return response;
    }
}


