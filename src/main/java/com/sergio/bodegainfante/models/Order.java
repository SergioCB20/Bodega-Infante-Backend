package com.sergio.bodegainfante.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergio.bodegainfante.models.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table( name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private Customer customer;
    @NotNull
    private OrderStatus status;
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;
    @Column(nullable = false)
    private LocalDateTime updated_at;
    @Column(nullable = true, updatable = true)
    private LocalDateTime deleted_at;
    @PrePersist
    public void prePersist() {
        created_at = LocalDateTime.now();
        updated_at = LocalDateTime.now();
        status = OrderStatus.CREATED;
    }
    public void markAsDeleted() {
        this.deleted_at = LocalDateTime.now();
        this.status = OrderStatus.CANCELLED;
    }
}
