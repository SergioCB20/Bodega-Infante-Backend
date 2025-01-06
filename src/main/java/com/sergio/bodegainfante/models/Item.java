package com.sergio.bodegainfante.models;

import com.sergio.bodegainfante.models.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long item_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @JsonIgnore
    private Order order;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", referencedColumnName = "package_id")
    private Package packageItem;

    private int quantity;

    private LocalDateTime deleted_at;

    @PrePersist
    public void prePersist() {
        if (deleted_at == null) {
            deleted_at = null;
        }
    }

    @PreUpdate
    public void preUpdate() {
        // Realiza actualizaciones necesarias antes de guardar
    }
}
