package com.warehouse.notification.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.warehouse.notification.entity.NotificationLog;
import com.warehouse.notification.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that listens for stock events and low-stock alerts.
 * Logs notifications to the console and persists them to the database.
 *
 * This demonstrates the consumer side of Kafka-based event-driven communication.
 * In a production system, this could send emails, SMS, or push notifications.
 */
@Component
public class StockEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockEventConsumer.class);

    private final NotificationLogRepository notificationLogRepository;
    private final ObjectMapper objectMapper;

    public StockEventConsumer(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Consumes stock events (STOCK_RECEIVED, STOCK_ISSUED, STOCK_TRANSFERRED).
     */
    @KafkaListener(topics = "${kafka.topic.stock-events}", groupId = "notification-group")
    public void consumeStockEvent(String eventJson) {
        log.info("Kafka event consumed from stock-events topic");

        try {
            JsonNode event = objectMapper.readTree(eventJson);
            String eventType = event.path("eventType").asText("UNKNOWN");
            String message = event.path("message").asText("No message");

            log.info("========================================");
            log.info("NOTIFICATION - Stock Event");
            log.info("Type: {}", eventType);
            log.info("Message: {}", message);
            log.info("========================================");

            // Persist to database
            NotificationLog notification = NotificationLog.builder()
                    .eventType(eventType)
                    .message(message)
                    .build();
            notificationLogRepository.save(notification);

        } catch (Exception e) {
            log.error("Failed to process stock event: {}", e.getMessage(), e);
        }
    }

    /**
     * Consumes low-stock alert events.
     */
    @KafkaListener(topics = "${kafka.topic.low-stock-events}", groupId = "notification-group")
    public void consumeLowStockEvent(String eventJson) {
        log.warn("Kafka event consumed from low-stock-events topic");

        try {
            JsonNode event = objectMapper.readTree(eventJson);
            String eventType = event.path("eventType").asText("LOW_STOCK");
            String message = event.path("message").asText("Low stock alert");
            int currentStock = event.path("currentStock").asInt(0);
            int minimumLevel = event.path("minimumStockLevel").asInt(0);

            log.warn("========================================");
            log.warn("⚠️  LOW STOCK ALERT");
            log.warn("Product ID: {}", event.path("productId").asLong());
            log.warn("Warehouse ID: {}", event.path("warehouseId").asLong());
            log.warn("Current Stock: {}", currentStock);
            log.warn("Minimum Level: {}", minimumLevel);
            log.warn("Message: {}", message);
            log.warn("========================================");

            // Persist to database
            NotificationLog notification = NotificationLog.builder()
                    .eventType(eventType)
                    .message(message)
                    .build();
            notificationLogRepository.save(notification);

        } catch (Exception e) {
            log.error("Failed to process low-stock event: {}", e.getMessage(), e);
        }
    }
}
