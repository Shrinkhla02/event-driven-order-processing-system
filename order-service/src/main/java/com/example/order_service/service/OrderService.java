package com.example.order_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.event.OrderEvent;
import com.example.order_service.kafka.OrderProducer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.order_service.model.OrderEntity;
import com.example.order_service.repository.OrderRepository;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    // Get specific order by userId and orderId
    public OrderResponseDTO getOrdersByOrderId(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findByUserIdAndOrderId(userId, orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
        return mapToResponseDTO(order);
    }

    // Get all orders by userId
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<OrderEntity> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for user " + userId);
        }
        return orders.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Create new order
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        OrderEntity orderEntity = OrderEntity.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderEntity saved = orderRepository.save(orderEntity);
        OrderEvent event = OrderEvent.builder()
                .orderId(saved.getOrderId())
                .userId(saved.getUserId())
                .productId(saved.getProductId())
                .quantity(saved.getQuantity())
                .totalPrice(saved.getTotalPrice())
                .status(saved.getStatus())
                .build();

        orderProducer.sendOrderEvent(event);

        return mapToResponseDTO(saved);
    }

    // Delete order by userId and orderId
    public void deleteOrderByOrderId(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findByUserIdAndOrderId(userId, orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
        orderRepository.deleteById(order.getOrderId());
    }

    // Private helper to convert Entity to ResponseDTO
    private OrderResponseDTO mapToResponseDTO(OrderEntity order) {
        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}