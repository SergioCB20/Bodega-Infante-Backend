package com.sergio.bodegainfante.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long product_id;

    @NotNull
    @Size(min = 3, max = 255)
    private String name;

    private String description;

    @NotNull
    private double price;

    private String image_url;

    @NotNull
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")  // El nombre de la columna de la llave foránea
    private Category category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    private LocalDateTime deleted_at;

    @OneToMany(mappedBy = "product")
    private List<PackageProduct> packageProducts;

    @PrePersist
    public void prePersist() {
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_at = LocalDateTime.now();
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDateTime getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }

    public List<PackageProduct> getPackageProducts() {
        return packageProducts;
    }

    public void setPackageProducts(List<PackageProduct> packageProducts) {
        this.packageProducts = packageProducts;
    }
}

