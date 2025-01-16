package com.sergio.bodegainfante.security;

import com.sergio.bodegainfante.security.jwt.JwtTokenFilter;
import com.sergio.bodegainfante.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        JwtUtils jwtUtils = new JwtUtils(); // Inicializamos manualmente JwtUtils
        return new JwtTokenFilter(jwtUtils); // Creamos el filtro con JwtUtils
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()  // Permite todas las solicitudes OPTIONS
                                .requestMatchers("/api/auth/**").permitAll()  // Rutas públicas de autenticación
                                .requestMatchers("/api/shop/**").permitAll()  // Rutas públicas de la tienda
                                .requestMatchers("/api/categories/**").permitAll()  // Rutas públicas para categorías
                                .requestMatchers("/api/products/**").permitAll()  // Rutas públicas para productos
                                .requestMatchers("/api/packages/**").permitAll()  // Rutas públicas para paquetes
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Solo accesible para ADMIN
                                .requestMatchers("/api/customers/**").hasRole("CUSTOMER")  // Solo accesible para CUSTOMER
                                .anyRequest().authenticated()  // Requiere autenticación para cualquier otra ruta
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Configura sesiones sin estado
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)  // Agrega filtro JWT
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                                })
                ).build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt password encryption
    }
}



