package com.sergio.bodegainfante.controllers;
import com.sergio.bodegainfante.dtos.LoginRequestDTO;
import com.sergio.bodegainfante.dtos.RegistrationRequestDTO;
import com.sergio.bodegainfante.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, String>> register(@Valid @RequestBody RegistrationRequestDTO registrationRequest,
                                            BindingResult result) {
        HashMap<String, String> response = new HashMap<>();
        if (result.hasErrors()) {
            HashMap<String, String> finalResponse = response;
            result.getAllErrors().forEach(error -> {
                finalResponse.put(error.getCode(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(finalResponse);
        }

        response = authService.register(registrationRequest);
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response); // Si hubo error (email ya en uso)
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, String>> login(@RequestBody LoginRequestDTO loginRequest) {
        HashMap<String, String> response = authService.login(loginRequest);

        // Si el login fue exitoso o falló, se devuelve una respuesta adecuada
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

}

