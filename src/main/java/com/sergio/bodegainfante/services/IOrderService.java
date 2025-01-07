package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.OrderDTO;
import com.sergio.bodegainfante.models.Order;

import java.util.List;

public interface IOrderService {
    List<Order> findAll();
    List<Order> findByCustomerId(Long customerId);
    Order createOrder(OrderDTO orderDTO, String customerEmail);
    Order updateOrder(OrderDTO orderDTO, Long orderId, String customerEmail);
    boolean deleteOrder(Long orderId, String customerEmail);
}
