package com.sergio.bodegainfante.controllers;

import com.sergio.bodegainfante.dtos.OrderDTO;
import com.sergio.bodegainfante.models.Order;
import com.sergio.bodegainfante.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable("customerId") Long customerId) {
        List<Order> orders = orderService.findByCustomerId(customerId);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO, @RequestParam String customerEmail) {
        try {
            Order createdOrder = orderService.createOrder(orderDTO, customerEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> updateOrder(@RequestBody OrderDTO orderDTO, @PathVariable("orderId") Long orderId, @RequestParam String customerEmail) {
        try {
            Order updatedOrder = orderService.updateOrder(orderDTO, orderId, customerEmail);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Long orderId, @RequestParam String customerEmail) {
        try {
            boolean isDeleted = orderService.deleteOrder(orderId, customerEmail);
            if (isDeleted) {
                return ResponseEntity.ok("Order deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized");
        }
    }
}

