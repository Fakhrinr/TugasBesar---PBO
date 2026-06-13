package com.crm.tubes.service;

import com.crm.tubes.model.Subscription;
import com.crm.tubes.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SubscriptionService = the class that handles BUSINESS LOGIC.
 *
 * @Service tells Spring Boot: "this class handles the logic/rules"
 *
 * The Service sits BETWEEN the Controller and the Repository:
 *   Controller → calls Service → Service calls Repository → Repository talks to DB
 *
 * The Controller should NOT talk to the Repository directly.
 * The Repository should NOT contain business logic.
 * Service is the middleman that keeps everything clean.
 */
@Service
public class SubscriptionService {

    /**
     * Spring automatically provides the Repository object here.
     * We never write: new SubscriptionRepository() — Spring handles it.
     */
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * getAllSubscriptions() — get all subscriptions AND auto-check their status.
     *
     * This is the key business logic:
     * Every time we load the list, we check each subscription's status
     * based on today's date (using checkStatus() from the Model).
     * If the status changed, we save the new status to the database.
     *
     * This way the status is ALWAYS up to date — no manual update needed.
     */
    public List<Subscription> getAllSubscriptions() {

        // 1. Get all subscriptions from database
        List<Subscription> list = subscriptionRepository.findAll();

        // 2. Loop through each subscription
        for (Subscription sub : list) {

            // Remember the old status before checking
            var oldStatus = sub.getStatus();

            // Call checkStatus() — this is the method in our Model class
            // It updates sub.status based on today vs endDate
            sub.checkStatus();

            // 3. If status changed, save the new status to database
            if (!sub.getStatus().equals(oldStatus)) {
                subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
            }
        }

        // 4. Return the updated list to the Controller
        return list;
    }

    /**
     * getSubscriptionById() — get one subscription by ID.
     * Used for the active plan card at the top of the page (Admin view).
     *
     * @param id the subscription ID
     */
    public Subscription getSubscriptionById(int id) {

        // Get from database
        Subscription sub = subscriptionRepository.findById(id);

        // Check and update status if needed
        var oldStatus = sub.getStatus();
        sub.checkStatus();
        if (!sub.getStatus().equals(oldStatus)) {
            subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
        }

        return sub;
    }

    /**
     * getActiveSubscriptionByCustomer() — get the active subscription
     * for a specific customer. Used for the Customer view.
     *
     * @param customerId the logged-in customer's ID
     */
    public Subscription getActiveSubscriptionByCustomer(int customerId) {

        Subscription sub = subscriptionRepository.findActiveByCustomerId(customerId);

        // Auto-check status
        var oldStatus = sub.getStatus();
        sub.checkStatus();
        if (!sub.getStatus().equals(oldStatus)) {
            subscriptionRepository.updateStatus(sub.getId(), sub.getStatus());
        }

        return sub;
    }
}
