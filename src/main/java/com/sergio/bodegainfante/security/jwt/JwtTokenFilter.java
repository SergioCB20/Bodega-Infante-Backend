package com.sergio.bodegainfante.security.jwt;

import com.sergio.bodegainfante.models.enums.Role;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = parseJwt(request);  // Obtén el token de los encabezados

        if (token != null && jwtUtils.validateToken(token)) {
            String email = jwtUtils.getUsernameFromToken(token);
            String roleS = jwtUtils.getRolesFromToken(token).get(0); // Obtener el rol del token

            // Solo procesamos si no existe una autenticación en el contexto
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = getAuthoritiesByRole(roleS);
                Role role = Role.fromString(roleS);  // Obtén el rol correspondiente
                UserDetailsImpl userDetails = new UserDetailsImpl(email, "", role, authorities);

                // Aquí configuramos la autenticación en el SecurityContext
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecemos la autenticación en el contexto
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);  // Continuar con el filtro
    }


    private List<GrantedAuthority> getAuthoritiesByRole(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("ROLE_CUSTOMER".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if ("ROLE_ADMIN".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        // Agregar más roles si es necesario
        return authorities;
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);  // Extraer el token del encabezado
        }
        return null;
    }
}


