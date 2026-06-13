package com.crm.tubes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crm.tubes.model.NotificationModel;
import com.crm.tubes.model.NotificationType;
import com.crm.tubes.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    @Transactional
    public NotificationModel createNotification(
            Integer userId,
            NotificationType type,
            String message
    ) {
        validateUserId(userId);
        validateType(type);
        validateMessage(message);

        NotificationModel notification = new NotificationModel();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);

        int savedRows = notificationRepository.save(notification);
        if (savedRows == 0) {
            throw new RuntimeException("Gagal menyimpan notifikasi.");
        }
        return notification;
    }

    public List<NotificationModel> getNotificationsByUserId(Integer userId) {
        validateUserId(userId);
        return notificationRepository.findByUserId(userId);
    }

    public List<NotificationModel> getUnreadNotificationsByUserId(Integer userId) {
        validateUserId(userId);
        return notificationRepository.findUnreadByUserId(userId);
    }

    public int countUnreadNotificationsByUserId(Integer userId) {
        validateUserId(userId);
        return notificationRepository.countUnreadByUserId(userId);
    }

    public List<NotificationModel> getNotificationsByType(Integer userId, NotificationType type) {
        validateUserId(userId);
        validateType(type);
        return notificationRepository.findByUserIdAndType(userId, type);
    }

    @Transactional
    public NotificationModel markAsRead(Integer notificationId, Integer userId){
        validateNotificationId(notificationId);
        validateUserId(userId);

        NotificationModel notification = getNotificationById(notificationId);
        validateOwnership(notification, userId);

        int updatedRows = notificationRepository.markAsRead(notificationId);
        if (updatedRows == 0) {
            throw new RuntimeException("Notif tidak ditemukan");
        }

        return getNotificationById(notificationId);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        validateUserId(userId);
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void deleteNotification(Integer notificationId, Integer userId) {
        validateNotificationId(notificationId);
        validateUserId(userId);

        NotificationModel notification = getNotificationById(notificationId);
        validateOwnership(notification, userId);

        int deletedRows = notificationRepository.deleteById(notificationId);
        if (deletedRows == 0) {
            throw new RuntimeException("Notifikasi tidak ditemukan.");
        }
    }

    @Transactional
    public NotificationModel notifyAccountStatus(Integer userId, String message) {
        return createNotification(userId, NotificationType.ACCOUNT_STATUS, message);
    }

    @Transactional
    public NotificationModel notifySubscriptionStatus(
            Integer userId,
            String planName,
            String status
    ) {
        String message = "Status subscription paket "
                + planName
                + " berubah menjadi "
                + status
                + ".";

        return createNotification(userId, NotificationType.SUBSCRIPTION_STATUS, message);
    }

    @Transactional
    public NotificationModel notifyInvoiceCreated(
            Integer userId,
            Integer invoiceId,
            BigDecimal totalAmount,
            LocalDate dueDate
    ) {
        String message = "Invoice ID "
                + invoiceId
                + " sebesar Rp"
                + totalAmount
                + " telah dibuat dan jatuh tempo pada "
                + dueDate
                + ".";

        return createNotification(userId, NotificationType.INVOICE_CREATED, message);
    }

    @Transactional
    public NotificationModel notifyPaymentDue(
            Integer userId,
            Integer invoiceId,
            LocalDate dueDate
    ) {
        String message = "Pembayaran invoice ID "+ invoiceId + " akan jatuh tempo pada " + dueDate + ".";

        return createNotification(userId, NotificationType.PAYMENT_DUE, message);
    }

    @Transactional
    public NotificationModel notifyPaymentOverdue(Integer userId, Integer invoiceId) {
        String message = "Pembayaran invoice ID " + invoiceId + " telah melewati jatuh tempo.";

        return createNotification(userId, NotificationType.PAYMENT_OVERDUE, message);
    }

    @Transactional
    public NotificationModel notifyPaymentSuccess(Integer userId, BigDecimal amount) {
        String message = "Pembayaran sebesar Rp" + amount + " berhasil diterima.";

        return createNotification(userId, NotificationType.PAYMENT_SUCCESS, message);
    }

    @Transactional
    public NotificationModel notifyTicketStatus(
            Integer userId,
            Integer ticketId,
            String status
    ) {
        String message = "Status ticket ID " + ticketId + " berubah menjadi " + status + ".";

        return createNotification(userId, NotificationType.TICKET_STATUS, message);
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID tidak boleh null.");
        }

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID harus lebih besar dari 0.");
        }
    }

    private void validateType(NotificationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipe notifikasi tidak boleh null.");
        }
    }

    private void validateMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Pesan notifikasi tidak boleh null.");
        }

        String trimmedMessage = message.trim();
        if (trimmedMessage.isEmpty()) {
            throw new IllegalArgumentException("Pesan notifikasi tidak boleh kosong.");
        }

        if (trimmedMessage.length() > 255) {
            throw new IllegalArgumentException("Pesan notifikasi maksimal 255 karakter.");
        }
    }

    private void validateNotificationId(Integer notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID tidak boleh null.");
        }

        if (notificationId <= 0) {
            throw new IllegalArgumentException("Notification ID harus lebih besar dari 0.");
        }
    }

    private NotificationModel getNotificationById(Integer notificationId) {
        return notificationRepository.findById(notificationId)
        .orElseThrow(() -> new RuntimeException("Notifikasi tidak ditemukan."));
    }

    private void validateOwnership(NotificationModel notification, Integer userId) {
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("User tidak memiliki akses ke notifikasi ini.");
        }
    }
}
