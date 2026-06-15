package com.crm.tubes.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crm.tubes.model.NotificationModel;
import com.crm.tubes.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public String showNotifications(
            @PathVariable Integer userId,
            Model model
    ) {
        List<NotificationModel> notifications =
                notificationService.getNotificationsByUserId(userId);

        model.addAttribute("notifications", notifications);
        model.addAttribute(
                "unreadCount",
                notificationService.countUnreadNotificationsByUserId(userId)
        );
        model.addAttribute("userId", userId);

        return "notification/list";
    }

    @PostMapping("/{notificationId}/read")
    public String markAsRead(
            @PathVariable Integer notificationId,
            @RequestParam Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            notificationService.markAsRead(notificationId, userId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Notifikasi berhasil ditandai sebagai dibaca."
            );
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/notifications/user/" + userId;
    }

    @PostMapping("/user/{userId}/read-all")
    public String markAllAsRead(
            @PathVariable Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            notificationService.markAllAsRead(userId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Semua notifikasi berhasil ditandai sebagai dibaca."
            );
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/notifications/user/" + userId;
    }

    @PostMapping("/{notificationId}/delete")
    public String deleteNotification(
            @PathVariable Integer notificationId,
            @RequestParam Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            notificationService.deleteNotification(notificationId, userId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Notifikasi berhasil dihapus."
            );
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/notifications/user/" + userId;
    }
}