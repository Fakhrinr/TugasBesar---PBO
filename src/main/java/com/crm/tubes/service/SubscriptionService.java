package com.crm.tubes.service;

import com.crm.tubes.model.Subscription;
import com.crm.tubes.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SubscriptionService — handle business logic subscription.
 *
 * @Service                 → Spring tau ini class logic bisnis
 * @RequiredArgsConstructor → Lombok inject Repository otomatis
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * getAllSubscriptions() — ambil semua subscription + auto cek status.
     * Dipakai untuk Admin/Teknisi view.
     */
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> list = subscriptionRepository.findAll();

        // Auto update status tiap subscription berdasarkan tanggal
        for (Subscription sub : list) {
            var oldStatus = sub.getStatus();
            sub.checkStatus(); // method dari Subscription.java
            if (!sub.getStatus().equals(oldStatus)) {
                subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
            }
        }
        return list;
    }

    /**
     * getSubscriptionsByCustomerId() — ambil semua subscription 1 customer.
     * Dipakai untuk history table di Customer view.
     *
     * @param customerId ID customer yang login
     */
    public List<Subscription> getSubscriptionsByCustomerId(int customerId) {
        List<Subscription> list = subscriptionRepository.findByCustomerId(customerId);

        // Auto update status juga
        for (Subscription sub : list) {
            var oldStatus = sub.getStatus();
            sub.checkStatus();
            if (!sub.getStatus().equals(oldStatus)) {
                subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
            }
        }
        return list;
    }

    /**
     * getActiveSubscriptionByCustomer() — ambil subscription aktif 1 customer.
     * Dipakai untuk card plan aktif di Customer view.
     *
     * @param customerId ID customer yang login
     */
    public Subscription getActiveSubscriptionByCustomer(int customerId) {
        Subscription sub = subscriptionRepository.findActiveByCustomerId(customerId);

        // Auto update status
        var oldStatus = sub.getStatus();
        sub.checkStatus();
        if (!sub.getStatus().equals(oldStatus)) {
            subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
        }
        return sub;
    }
}