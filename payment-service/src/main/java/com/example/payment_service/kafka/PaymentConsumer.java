package com.example.payment_service.kafka;

import com.example.payment_service.entity.ProcessedEvent;
import com.example.payment_service.event.OrderEvent;
import com.example.payment_service.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentConsumer {
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;

    @RetryableTopic(
            attempts = "4",
            // 1 original + 3 retries

            backoff = @Backoff(delay = 1000),
            // wait 1 second between retries

            dltTopicSuffix = ".DLQ",
            // failed messages go to order-created.DLQ

            topicSuffixingStrategy =
                    TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
            // retry topics:
            // order-created-retry-0
            // order-created-retry-1
            // order-created-retry-2
    )
    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void processPayment(String message) {
        try {
            // Step 1 — convert JSON to OrderEvent
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            log.info("Payment Service received order event for orderId: {}",
                    event.getOrderId());

            // Step 2 — Idempotency check
            // check if this order was already processed
            if (processedEventRepository.existsById(event.getOrderId())) {
                log.warn("Duplicate event received for orderId: {} skipping",
                        event.getOrderId());
                return;  // skip duplicate — do not process again
            }

            // Step 3 — Process payment
            log.info("Processing payment for userId: {} amount: {}",
                    event.getUserId(), event.getTotalPrice());

            // uncomment to test retry and DLQ
            // throw new RuntimeException("Simulating failure for DLQ test");

            // Step 4 — Save as processed for idempotency
            processedEventRepository.save(ProcessedEvent.builder()
                    .orderId(event.getOrderId())
                    .processedAt(LocalDateTime.now())
                    .build());

            log.info("Payment successful for orderId: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Error processing payment for orderId - error: {}",
                    e.getMessage());
            // rethrow so RetryableTopic triggers retries
            throw new RuntimeException(e);
        }
    }
    @DltHandler
    public void handleDLQ(String message) {
        log.error("Failed message in DLQ needs investigation: {}", message);
        // in real world:
        // → send Slack alert
        // → create PagerDuty incident
        // → store in database for manual review
    }
}