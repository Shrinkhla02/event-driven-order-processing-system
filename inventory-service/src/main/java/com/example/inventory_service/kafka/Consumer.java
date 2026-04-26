package com.example.inventory_service.kafka;

import com.example.inventory_service.event.OrderEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class Consumer {

    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    public void updateInventory(String message) throws JsonProcessingException {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("Notification Service received order event for orderId: {}", event.getOrderId());
            log.info("Sending notification to userId: {} for orderId: {}",
                    event.getUserId(), event.getOrderId());
            log.info("Notification sent successfully for orderId: {}", event.getOrderId());
        }catch(Exception e){
            log.error("Error sending notification: {}", e.getMessage());
        }

    }
}
