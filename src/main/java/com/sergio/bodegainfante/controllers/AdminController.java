package com.sergio.bodegainfante.controllers;

import com.sergio.bodegainfante.dtos.AdminInfoDTO;
import com.sergio.bodegainfante.dtos.CustomerInfoDTO;
import com.sergio.bodegainfante.models.Modification;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import com.sergio.bodegainfante.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerInfoDTO>> getAllCustomers() {
        List<CustomerInfoDTO> customers = adminService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
    @GetMapping("/admins")
    public ResponseEntity<List<AdminInfoDTO>> getAllAdmins() {
        List<AdminInfoDTO> admins = adminService.getAllAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @PutMapping("/users/{email}/role")
    public ResponseEntity<String> updateUserRole(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @PathVariable("email") String email, @RequestBody Map<String, String> body) {
        String newRole = body.get("newRole");
        boolean updated = adminService.updateUserRole(email, newRole,userDetails.getUsername());
        if (updated) {
            return ResponseEntity.ok("User role updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or role unchanged");
    }

    @GetMapping("/modifications")
    public ResponseEntity<List<Modification>> getFilteredModifications(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long adminId) {

        List<Modification> modifications = adminService.getFilteredModifications(startDate, endDate, adminId);

        if (modifications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(modifications);
        }

        return ResponseEntity.ok(modifications);
    }

}
