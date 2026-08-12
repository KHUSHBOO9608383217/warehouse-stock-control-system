package com.warehouse.stockmovement.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.warehouse.stockmovement.dto.event.StockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer for publishing stock events.
 * Publishes to "stock-events" and "low-stock-events" topics.
 */
@Component
public class StockEventProducer {

    private static final Logger log = LoggerFactory.getLogger(StockEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.stock-events}")
    private String stockEventsTopic;

    @Value("${kafka.topic.low-stock-events}")
    private String lowStockEventsTopic;

    public StockEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Publishes a stock event (received, issued, transferred) to Kafka.
     */
    public void publishStockEvent(StockEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(stockEventsTopic, eventJson);
            log.info("Kafka event published to topic '{}': type={}, productId={}, quantity={}",
                    stockEventsTopic, event.getEventType(), event.getProductId(), event.getQuantity());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stock event", e);
        }
    }

    /**
     * Publishes a low-stock alert event to Kafka.
     */
    public void publishLowStockEvent(StockEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(lowStockEventsTopic, eventJson);
            log.warn("LOW STOCK ALERT published to topic '{}': productId={}, currentStock={}, minimumLevel={}",
                    lowStockEventsTopic, event.getProductId(), event.getCurrentStock(),
                    event.getMinimumStockLevel());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize low-stock event", e);
        }
    }
}
