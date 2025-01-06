package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
