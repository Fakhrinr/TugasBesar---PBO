package com.crm.tubes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.SubscriptionStatus;
import com.crm.tubes.repository.InvoiceRepository;
import com.crm.tubes.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository; // ← tambah ini

    public List<Subscription> getAllSubscriptions() {
        List<Subscription> list = subscriptionRepository.findAll();

        for (Subscription sub : list) {
            var oldStatus = sub.getStatus();
            sub.checkStatus();
            if (!sub.getStatus().equals(oldStatus)) {
                subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());

                // ← Auto generate invoice jika berubah ke GRACE atau SUSPENDED
                if (sub.getStatus() == SubscriptionStatus.GRACE ||
                    sub.getStatus() == SubscriptionStatus.SUSPENDED) {
                    generateInvoiceForSubscription(sub);
                }
            }
        }
        return list;
    }

    public List<Subscription> getSubscriptionsByCustomerId(int customerId) {
        List<Subscription> list = subscriptionRepository.findByCustomerId(customerId);

        for (Subscription sub : list) {
            var oldStatus = sub.getStatus();
            sub.checkStatus();
            if (!sub.getStatus().equals(oldStatus)) {
                subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());

                // ← Auto generate invoice juga
                if (sub.getStatus() == SubscriptionStatus.GRACE ||
                    sub.getStatus() == SubscriptionStatus.SUSPENDED) {
                    generateInvoiceForSubscription(sub);
                }
            }
        }
        return list;
    }

    public Subscription getActiveSubscriptionByCustomer(int customerId) {
        Subscription sub = subscriptionRepository.findActiveByCustomerId(customerId);

        var oldStatus = sub.getStatus();
        sub.checkStatus();
        if (!sub.getStatus().equals(oldStatus)) {
            subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());

            // ← Auto generate invoice juga
            if (sub.getStatus() == SubscriptionStatus.GRACE ||
                sub.getStatus() == SubscriptionStatus.SUSPENDED) {
                generateInvoiceForSubscription(sub);
            }
        }
        return sub;
    }

    public void activateSubscription(int subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId);
        subscription.renewFromNow();
        subscriptionRepository.updateFullSubscription(subscription);
    }

    // ── Helper: generate invoice dari subscription ────────────────────────
    private void generateInvoiceForSubscription(Subscription sub) {
        Invoice invoice = new Invoice();
        invoice.setSubscription(sub);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(7)); // due 7 hari
        invoice.setTotalAmount(BigDecimal.valueOf(sub.getMonthlyFee()));
        invoice.setLateFeeAmount(BigDecimal.ZERO);
        invoice.generateInvoice(); // set status OPEN

        invoiceRepository.save(invoice);
    }
}