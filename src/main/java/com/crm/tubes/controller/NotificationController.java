package com.crm.tubes.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crm.tubes.model.NotificationModel;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.AuthService;
import com.crm.tubes.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    public String showMyNotifications(
            HttpSession session,
            Model model
    ) {
        UserModel loggedUser = authService.getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Integer userId = loggedUser.getId();

        List<NotificationModel> notifications =
                notificationService.getNotificationsByUserId(userId);

        model.addAttribute("notifications", notifications);
        model.addAttribute(
                "unreadCount",
                notificationService.countUnreadNotificationsByUserId(userId)
        );
        model.addAttribute("userId", userId);
        model.addAttribute("loggedUser", loggedUser);

        return "notification/list";
    }

    @GetMapping("/user/{userId}")
    public String showNotifications(
            @PathVariable Integer userId,
            HttpSession session
    ) {
        UserModel loggedUser = authService.getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/login";
        }

        if (!loggedUser.getId().equals(userId)) {
            return "redirect:/notifications";
        }

        return "redirect:/notifications";
    }

    @PostMapping("/{notificationId}/read")
    public String markAsRead(
            @PathVariable Integer notificationId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserModel loggedUser = authService.getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            notificationService.markAsRead(notificationId, loggedUser.getId());
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

        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserModel loggedUser = authService.getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            notificationService.markAllAsRead(loggedUser.getId());
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

        return "redirect:/notifications";
    }

    @PostMapping("/{notificationId}/delete")
    public String deleteNotification(
            @PathVariable Integer notificationId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserModel loggedUser = authService.getLoggedUser(session);
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            notificationService.deleteNotification(notificationId, loggedUser.getId());
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

        return "redirect:/notifications";
    }
}