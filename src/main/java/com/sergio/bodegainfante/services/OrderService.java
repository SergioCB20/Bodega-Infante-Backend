package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.ItemDTO;
import com.sergio.bodegainfante.dtos.OrderDTO;
import com.sergio.bodegainfante.exceptions.OrderNotFoundException;
import com.sergio.bodegainfante.exceptions.PackageNotFoundException;
import com.sergio.bodegainfante.exceptions.ProductNotFoundException;
import com.sergio.bodegainfante.exceptions.UnauthorizedAccessException;
import com.sergio.bodegainfante.models.*;
import com.sergio.bodegainfante.models.enums.ItemType;
import com.sergio.bodegainfante.models.enums.OrderStatus;
import com.sergio.bodegainfante.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sergio.bodegainfante.models.Package;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private ModificationRepository modificationRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findByCustomerId(Long customerId) {
        Optional<User> user = userRepository.findById(customerId);
        if(user.isPresent()) {
            Customer customer = (Customer) user.get();
            return orderRepository.findByCustomer(customer);
        }
        return null;
    }

    public Order createOrder(OrderDTO orderDTO, String customerEmail) {
        // Crear nueva orden
        Order order = new Order();

        // Asignar el cliente a la orden si se proporciona un email
        if (customerEmail != null) {
            Optional<User> user = userRepository.findByEmail(customerEmail);
            if (user.isPresent()) {
                Customer customer = (Customer) user.get();
                order.setCustomer(customer);
            }
        } else {
            order.setCostumerPhoneNumber(orderDTO.getPhoneNumber());
        }

        List<Item> items = new ArrayList<>();

        for (ItemDTO itemDTO : orderDTO.getItems()) {
            Item item = new Item();
            // Verificar si el tipo es PRODUCT o PACKAGE
            if (itemDTO.getItemType().equals(ItemType.PRODUCT)) {
                // Buscar el producto en el repositorio
                Optional<Product> productOptional = productRepository.findById(itemDTO.getProductId());
                if (productOptional.isPresent()) {
                    Product product = productOptional.get();
                    item.setProduct(product);
                    item.setQuantity(itemDTO.getQuantity());
                } else {
                    throw new ProductNotFoundException("Product with ID " + itemDTO.getProductId() + " not found.");
                }
            } else if (itemDTO.getItemType().equals(ItemType.PACKAGE)) {
                // Buscar el paquete en el repositorio
                Optional<Package> packageOptional = packageRepository.findById(itemDTO.getPackageId());
                if (packageOptional.isPresent()) {
                    Package pkg = packageOptional.get();
                    item.setPackageItem(pkg);
                    item.setQuantity(itemDTO.getQuantity());
                } else {
                    throw new PackageNotFoundException("Package with ID " + itemDTO.getPackageId() + " not found.");
                }
            } else {
                throw new IllegalArgumentException("Invalid item type.");
            }
            item.setType(itemDTO.getItemType());
            item.setDeleted_at(null);
            item.setOrder(order);
            items.add(item);
            itemRepository.save(item);
        }

        order.setItems(items);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(OrderDTO orderDTO, Long orderId, String customerEmail) {
        // Buscar la orden por su ID
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }

        Order order = optionalOrder.get();

        // Si el correo del cliente es proporcionado, actualizar el cliente asociado
        if (customerEmail != null) {
            Optional<User> user = userRepository.findByEmail(customerEmail);
            if (user.isPresent() && user.get() instanceof Customer) {
                Customer customer = (Customer) user.get();
                order.setCustomer(customer);  // Asignar el cliente a la orden
            } else {
                throw new UnauthorizedAccessException("User is not a customer or does not exist.");
            }
        } else {
            order.setCostumerPhoneNumber(orderDTO.getPhoneNumber());
        }

        // Actualizar la lista de productos
        List<Item> updatedItems = new ArrayList<>();
        for (ItemDTO itemDTO : orderDTO.getItems()) {
            Item item = new Item();
            item.setQuantity(itemDTO.getQuantity());

            // Verificar si el tipo es PRODUCT o PACKAGE
            if (itemDTO.getItemType().equals(ItemType.PRODUCT)) {
                Optional<Product> productOptional = productRepository.findById(itemDTO.getProductId());
                if (productOptional.isPresent()) {
                    Product product = productOptional.get();
                    item.setProduct(product);
                } else {
                    throw new ProductNotFoundException("Product with ID " + itemDTO.getProductId() + " not found.");
                }
            } else if (itemDTO.getItemType().equals(ItemType.PACKAGE)) {
                Optional<Package> packageOptional = packageRepository.findById(itemDTO.getPackageId());
                if (packageOptional.isPresent()) {
                    Package pkg = packageOptional.get();
                    item.setPackageItem(pkg);
                } else {
                    throw new PackageNotFoundException("Package with ID " + itemDTO.getPackageId() + " not found.");
                }
            } else {
                throw new IllegalArgumentException("Invalid item type.");
            }

            item.setType(itemDTO.getItemType());
            item.setOrder(order);  // Asignar la orden al item
            updatedItems.add(item);
        }

        // Asignar los items actualizados a la orden
        order.setItems(updatedItems);

        // Actualizar la fecha de actualización de la orden
        order.setUpdated_at(LocalDateTime.now());

        // Guardar la orden actualizada
        Order updatedOrder = orderRepository.save(order);

        // Registrar la modificación
        Optional<User> user = userRepository.findByEmail(customerEmail);
        if (user.isPresent() && user.get() instanceof Admin) {
            Admin admin = (Admin) user.get();
            Modification modification = new Modification();
            modification.setAdmin(admin);
            modification.setDescription("Order with ID " + orderId + " updated");
            modificationRepository.save(modification);
        }

        return updatedOrder;
    }

    @Transactional
    public boolean deleteOrder(Long orderId, String customerEmail) {
        // Buscar la orden por su ID
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }

        Order order = optionalOrder.get();

        // Si el correo del cliente es proporcionado, verificar que el cliente es el propietario de la orden
        if (customerEmail != null) {
            Optional<User> user = userRepository.findByEmail(customerEmail);
            if (user.isPresent() && user.get() instanceof Customer) {
                Customer customer = (Customer) user.get();
                if (!order.getCustomer().equals(customer)) {
                    throw new UnauthorizedAccessException("User is not authorized to delete this order.");
                }
            } else {
                throw new UnauthorizedAccessException("User is not a customer or does not exist.");
            }
        }

        // Marcar la orden como eliminada (eliminación lógica)
        order.setDeleted_at(LocalDateTime.now());
        order.setUpdated_at(LocalDateTime.now());  // Actualizar la fecha de actualización
        orderRepository.save(order);  // Guardar los cambios en la base de datos

        // Registrar la modificación en el historial
        Optional<User> user = userRepository.findByEmail(customerEmail);
        if (user.isPresent() && user.get() instanceof Admin) {
            Admin admin = (Admin) user.get();
            Modification modification = new Modification();
            modification.setAdmin(admin);
            modification.setDescription("Order with ID " + orderId + " logically deleted");
            modificationRepository.save(modification);
        }

        return true;
    }

}
