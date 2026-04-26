package com.example.order_service.kafka;

import com.example.order_service.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderEvent(OrderEvent event) {
        log.info("Sending order event for orderId: {}", event.getOrderId());
        kafkaTemplate.send("order-created", event);
        log.info("Order event sent successfully for orderId: {}", event.getOrderId());
    }
}