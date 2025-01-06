package com.sergio.bodegainfante.security;

import com.sergio.bodegainfante.dtos.LoginRequestDTO;
import com.sergio.bodegainfante.dtos.RegistrationRequestDTO;
import org.springframework.security.core.Authentication;
import java.util.HashMap;

public interface IAuthService {
    HashMap<String, String> register(RegistrationRequestDTO registrationRequest);
    HashMap<String, String> login(LoginRequestDTO loginRequest);
}

