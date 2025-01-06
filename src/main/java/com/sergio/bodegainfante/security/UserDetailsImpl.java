package com.sergio.bodegainfante.security;
import com.sergio.bodegainfante.models.Admin;
import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.models.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {

    private String email;
    private String password;
    @Getter
    private Role role;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String email, String password, Role role, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new UserDetailsImpl(
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                Collections.singletonList(authority)
        );
    }
    public static UserDetailsImpl build(Customer customer) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + customer.getRole().name());

        return new UserDetailsImpl(
                customer.getEmail(),
                customer.getPassword(),
                customer.getRole(),
                Collections.singletonList(authority)
        );
    }

    public static UserDetailsImpl build(Admin admin) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + admin.getRole().name());

        return new UserDetailsImpl(
                admin.getEmail(),
                admin.getPassword(),
                admin.getRole(),
                Collections.singletonList(authority)
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;  // Devolvemos el email como 'username' para Spring Security
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;  // Aquí podrías implementar lógica para verificar si la cuenta está habilitada
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    // Si lo necesitas, puedes implementar setDetails aquí para manejar detalles adicionales de autenticación.
    public void setDetails(WebAuthenticationDetails webAuthenticationDetails) {
        // Implementar si es necesario
    }

    public String getRole(){
        return role.getDisplayName();
    }

}

