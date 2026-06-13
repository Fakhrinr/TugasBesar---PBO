package com.crm.tubes.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crm.tubes.model.NotificationModel;
import com.crm.tubes.model.NotificationType;
import com.crm.tubes.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor

public class NotificationController {
    
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody NotificationModel request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("Request notifikasi tidak boleh null.");
            }

            NotificationModel notification = notificationService.createNotification(
                    request.getUserId(),
                    request.getType(),
                    request.getMessage()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotificationsByUserId(@PathVariable Integer userId) {
        try {
            List<NotificationModel> notifications =
                    notificationService.getNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable Integer userId) {
        try {
            List<NotificationModel> notifications =
                    notificationService.getUnreadNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<?> countUnreadNotifications(@PathVariable Integer userId) {
        try {
            int count = notificationService.countUnreadNotificationsByUserId(userId);
            return ResponseEntity.ok(Map.of("unreadCount", count));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<?> getNotificationsByType(
            @PathVariable Integer userId,
            @PathVariable String type
    ) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type);
            List<NotificationModel> notifications =
                    notificationService.getNotificationsByType(userId, notificationType);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Integer notificationId,
            @RequestParam Integer userId
    ) {
        try {
            NotificationModel notification =
                    notificationService.markAsRead(notificationId, userId);
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable Integer userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(
                    Map.of("message", "Semua notifikasi berhasil ditandai sebagai dibaca.")
            );
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Integer notificationId,
            @RequestParam Integer userId
    ) {
        try {
            notificationService.deleteNotification(notificationId, userId);
            return ResponseEntity.ok(Map.of("message", "Notifikasi berhasil dihapus."));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", exception.getMessage()));
        }
    }
}





