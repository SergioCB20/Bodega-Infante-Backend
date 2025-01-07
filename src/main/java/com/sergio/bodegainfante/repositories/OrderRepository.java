package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.Customer;
import com.sergio.bodegainfante.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(Customer customer);
}
