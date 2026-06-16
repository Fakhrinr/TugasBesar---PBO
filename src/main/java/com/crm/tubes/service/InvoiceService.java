package com.crm.tubes.service;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.InvoiceStatus;
import com.crm.tubes.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;
    public InvoiceService(InvoiceRepository invoiceRepository,
                      NotificationService notificationService,
                      SubscriptionService subscriptionService) {

    this.invoiceRepository = invoiceRepository;
    this.notificationService = notificationService;
    this.subscriptionService = subscriptionService;
    }
    /**
     * Create new invoice
     */
    public void createInvoice(Invoice invoice) {
        invoice.generateInvoice();
        invoiceRepository.save(invoice);
    }

    /**
     * Get all invoices
     */
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    /**
     * Get invoices by customer ID
     * Dipakai oleh Customer (hanya lihat miliknya)
     */
    public List<Invoice> getInvoicesByCustomerId(int customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }

    /**
     * Get invoice by id
     */
    public Invoice getInvoiceById(int id) {
        return invoiceRepository.findById(id);
    }

    /**
     * Apply late fee to invoice
     */
    public void applyLateFee(int invoiceId, BigDecimal fee) {
        Invoice invoice = invoiceRepository.findById(invoiceId);
        invoice.applyLateFee(fee);
        invoiceRepository.updateLateFee(invoiceId, invoice.getLateFeeAmount());

        // ← Tambah notifikasi ke customer
        int userId = invoice.getSubscription().getCustomer().getId();
        notificationService.notifyPaymentOverdue(userId, invoiceId);
    }

    /**
     * Mark invoice as paid
     */
    public void markAsPaid(int invoiceId) {

    Invoice invoice = invoiceRepository.findById(invoiceId);

    invoice.markAsPaid();

    invoiceRepository.updateStatus(
            invoiceId,
            InvoiceStatus.PAID
    );


    // AKTIFKAN SUBSCRIPTION SETELAH PEMBAYARAN
    subscriptionService.activateSubscription(
            invoice.getSubscription().getId()
    );


    int userId = invoice.getSubscription()
            .getCustomer()
            .getId();

    BigDecimal total = invoice.calculateTotal();

    notificationService.notifyPaymentSuccess(
            userId,
            total
    );
}

    /**
     * Check invoice status
     */
    public void checkInvoiceStatus(int invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId);

        invoice.checkStatus();

        invoiceRepository.updateStatus(
                invoiceId,
                invoice.getStatus()
        );
    }
}