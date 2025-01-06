package com.sergio.bodegainfante.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public int getNumber_modifications() {
        return number_modifications;
    }

    public void setNumber_modifications(int number_modifications) {
        this.number_modifications = number_modifications;
    }

    public LocalDateTime getLast_modification_at() {
        return last_modification_at;
    }

    public void setLast_modification_at(LocalDateTime last_modification_at) {
        this.last_modification_at = last_modification_at;
    }

    public List<Modification> getModifications() {
        return modifications;
    }

    public void setModifications(List<Modification> modifications) {
        this.modifications = modifications;
    }

    private int number_modifications;
    private LocalDateTime last_modification_at;
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Modification> modifications;
}