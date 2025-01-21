package com.sergio.bodegainfante.dtos;

import com.sergio.bodegainfante.models.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductDTO {
    private Long productId;
    @NotNull
    @Size(min = 3, max = 255)
    private String name;
    private String description;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @NotNull
    private double price;
    private String image_url;
    @NotNull
    private String categoryName;

    public ProductDTO(String name, String description, double price, String image_url, String categoryName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.categoryName = categoryName;
    }

    public ProductDTO(String name, String description, double price, String categoryName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryName = categoryName;
    }

    public ProductDTO(Product product) {
        this.productId = product.getProduct_id();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.image_url = product.getImage_url();
        this.categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "Categoria borrada";
    }


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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
