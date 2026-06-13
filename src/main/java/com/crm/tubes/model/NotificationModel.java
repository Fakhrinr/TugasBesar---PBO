package com.crm.tubes.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationModel {

    private Integer id;
    private Integer userId;
    private NotificationType type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}