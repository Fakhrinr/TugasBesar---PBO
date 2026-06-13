package com.crm.tubes.service;

import com.crm.tubes.model.Subscription;
import com.crm.tubes.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public List<Subscription> getAllSubscriptions() {

        List<Subscription> list = subscriptionRepository.findAll();

        for (Subscription sub : list) {

            var oldStatus = sub.getStatus();

            sub.checkStatus();

            if (!sub.getStatus().equals(oldStatus)) {
                subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
            }
        }

        return list;
    }


    public Subscription getSubscriptionById(int id) {

        Subscription sub = subscriptionRepository.findById(id);

        var oldStatus = sub.getStatus();
        sub.checkStatus();
        if (!sub.getStatus().equals(oldStatus)) {
            subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
        }

        return sub;
    }

    public Subscription getActiveSubscriptionByCustomer(int customerId) {

        Subscription sub = subscriptionRepository.findActiveByCustomerId(customerId);

        var oldStatus = sub.getStatus();
        sub.checkStatus();
        if (!sub.getStatus().equals(oldStatus)) {
            subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
        }

        return sub;
    }
}
