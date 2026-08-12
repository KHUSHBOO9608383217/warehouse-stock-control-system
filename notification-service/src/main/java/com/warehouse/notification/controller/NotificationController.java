package com.warehouse.notification.controller;

import com.warehouse.notification.dto.ApiResponse;
import com.warehouse.notification.dto.NotificationResponse;
import com.warehouse.notification.repository.NotificationLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "APIs for viewing notification logs")
public class NotificationController {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationController(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Returns all notification logs, newest first")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(log -> NotificationResponse.builder()
                        .id(log.getId())
                        .eventType(log.getEventType())
                        .message(log.getMessage())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }
}
