package com.sergio.bodegainfante.controllers;

import com.sergio.bodegainfante.dtos.AdminInfoDTO;
import com.sergio.bodegainfante.dtos.CustomerInfoDTO;
import com.sergio.bodegainfante.dtos.UserUpdateDTO;
import com.sergio.bodegainfante.models.Admin;
import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.User;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                AdminInfoDTO adminInfoDTO = userService.getAdminInfo(email);
                return new ResponseEntity<>(adminInfoDTO, HttpStatus.OK);
            }

            CustomerInfoDTO customerInfoDTO = userService.getCustomerInfo(email);
            return new ResponseEntity<>(customerInfoDTO, HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserUpdateDTO userUpdateDTO) {

        if (userDetails != null) {
            String email = userDetails.getUsername();
            UserUpdateDTO updatedUser = userService.updateUserInfo(email, userUpdateDTO);

            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
    }
}
