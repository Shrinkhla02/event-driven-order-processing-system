package com.example.order_service.controller;

import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.order_service.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrders(@PathVariable Long userId, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrdersByOrderId(userId, orderId));
        // Logic to retrieve orders for the specified user

    }

     @PostMapping("/{userId}/orders")
     public ResponseEntity<OrderResponseDTO> createOrder(
             @PathVariable Long userId,
             @RequestBody OrderRequestDTO request) {

         request.setUserId(userId);
         OrderResponseDTO response = orderService.createOrder(request);
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
     }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        // Logic to retrieve orders for the specified user

    }

    @DeleteMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<String> deleteOrderByOrderId(@PathVariable Long userId, @PathVariable Long orderId){
        orderService.deleteOrderByOrderId(userId,orderId);
        return ResponseEntity.ok("Order deleted successfully") ;
    }


    }







