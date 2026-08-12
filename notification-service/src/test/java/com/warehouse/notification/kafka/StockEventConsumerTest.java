package com.warehouse.notification.kafka;

import com.warehouse.notification.entity.NotificationLog;
import com.warehouse.notification.repository.NotificationLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockEventConsumer Unit Tests")
class StockEventConsumerTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private StockEventConsumer stockEventConsumer;

    @Test
    @DisplayName("Should consume stock event and save notification")
    void consumeStockEvent_Success() {
        String eventJson = "{\"eventType\":\"STOCK_RECEIVED\",\"productId\":1,"
                + "\"warehouseId\":1,\"quantity\":100,"
                + "\"message\":\"Received 100 units of product 1 into warehouse 1\"}";

        when(notificationLogRepository.save(any())).thenReturn(new NotificationLog());

        stockEventConsumer.consumeStockEvent(eventJson);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(captor.capture());

        NotificationLog saved = captor.getValue();
        assertEquals("STOCK_RECEIVED", saved.getEventType());
        assertTrue(saved.getMessage().contains("Received 100 units"));
    }

    @Test
    @DisplayName("Should consume low-stock event and save notification")
    void consumeLowStockEvent_Success() {
        String eventJson = "{\"eventType\":\"LOW_STOCK\",\"productId\":1,"
                + "\"warehouseId\":1,\"currentStock\":15,\"minimumStockLevel\":20,"
                + "\"message\":\"LOW STOCK ALERT: Product 1 in Warehouse 1. Current: 15, Minimum: 20\"}";

        when(notificationLogRepository.save(any())).thenReturn(new NotificationLog());

        stockEventConsumer.consumeLowStockEvent(eventJson);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(captor.capture());

        NotificationLog saved = captor.getValue();
        assertEquals("LOW_STOCK", saved.getEventType());
        assertTrue(saved.getMessage().contains("LOW STOCK ALERT"));
    }

    @Test
    @DisplayName("Should handle malformed event JSON gracefully")
    void consumeStockEvent_MalformedJson() {
        String malformedJson = "not a valid json";

        // Should not throw an exception - handles it internally
        assertDoesNotThrow(() -> stockEventConsumer.consumeStockEvent(malformedJson));
    }
}
