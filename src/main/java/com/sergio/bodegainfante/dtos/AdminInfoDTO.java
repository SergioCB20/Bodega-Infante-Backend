package com.sergio.bodegainfante.dtos;

import com.sergio.bodegainfante.models.Modification;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class AdminInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime last_modification_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Modification> getModifications() {
        return modifications;
    }

    public void setModifications(List<Modification> modifications) {
        this.modifications = modifications;
    }

    public LocalDateTime getLast_modification_at() {
        return last_modification_at;
    }

    public void setLast_modification_at(LocalDateTime last_modification_at) {
        this.last_modification_at = last_modification_at;
    }

    private List<Modification> modifications;
}
