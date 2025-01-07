package com.sergio.bodegainfante.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PackageDTO {

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null")
    private double price;

    private String image_url;

    @NotNull(message = "Availability status cannot be null")
    private boolean available;

    public List<PackageProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<PackageProductDTO> products) {
        this.products = products;
    }

    @NotEmpty
    private List<PackageProductDTO>products;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}

