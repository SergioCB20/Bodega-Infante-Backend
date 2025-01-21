package com.sergio.bodegainfante.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class PackageProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pxp_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", referencedColumnName = "package_id")
    @JsonBackReference
    private Package pkg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @JsonBackReference
    private Product product;

    @NotNull
    private int quantity;

    public Long getPxp_id() {
        return pxp_id;
    }

    public void setPxp_id(Long pxp_id) {
        this.pxp_id = pxp_id;
    }

    public Package getPkg() {
        return pkg;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

