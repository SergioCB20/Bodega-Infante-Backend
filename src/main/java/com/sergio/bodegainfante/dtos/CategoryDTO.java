package com.sergio.bodegainfante.dtos;

import com.sergio.bodegainfante.models.Product;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CategoryDTO {
    @NotNull
    @Size(min = 3, max = 255)
    private String name;
    @NotNull
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
