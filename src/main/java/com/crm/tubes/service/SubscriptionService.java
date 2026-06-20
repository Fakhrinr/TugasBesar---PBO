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
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;


    public List<Subscription> getAllSubscriptions() {

        List<Subscription> list = subscriptionRepository.findAll();

        for (Subscription sub : list) {

            SubscriptionStatus oldStatus = sub.getStatus();

            sub.checkStatus();

            if (!sub.getStatus().equals(oldStatus)) {

                subscriptionRepository.updateStatus(
                    sub.getId(),
                    sub.getStatus()
                );
                notificationService.notifySubscriptionStatus(
                    sub.getCustomer().getId(),
                    sub.getPlanName(),
                    sub.getStatus().name()
                );

                if (sub.getStatus() == SubscriptionStatus.GRACE ||
                    sub.getStatus() == SubscriptionStatus.SUSPENDED) {

                    generateInvoiceForSubscription(sub);
                }
            }
        }

        return list;
    }



    public List<Subscription> getSubscriptionsByCustomerId(int customerId) {

        List<Subscription> list =
                subscriptionRepository.findByCustomerId(customerId);


        for (Subscription sub : list) {

            SubscriptionStatus oldStatus = sub.getStatus();

            sub.checkStatus();

            if (!sub.getStatus().equals(oldStatus)) {

                subscriptionRepository.updateStatus(
                    sub.getId(),
                    sub.getStatus()
                );
                notificationService.notifySubscriptionStatus(
                    sub.getCustomer().getId(),
                    sub.getPlanName(),
                    sub.getStatus().name()
                );


                if (sub.getStatus() == SubscriptionStatus.GRACE ||
                    sub.getStatus() == SubscriptionStatus.SUSPENDED) {

                    generateInvoiceForSubscription(sub);
                }
            }
        }

        return list;
    }



    public Subscription getActiveSubscriptionByCustomer(int customerId) {

        Subscription sub =
                subscriptionRepository.findActiveByCustomerId(customerId);


        SubscriptionStatus oldStatus = sub.getStatus();

        sub.checkStatus();


        if (!sub.getStatus().equals(oldStatus)) {

            subscriptionRepository.updateStatus(
                sub.getId(),
                sub.getStatus()
            );
            notificationService.notifySubscriptionStatus(
                sub.getCustomer().getId(),
                sub.getPlanName(),
                sub.getStatus().name()
            );


            if (sub.getStatus() == SubscriptionStatus.GRACE ||
                sub.getStatus() == SubscriptionStatus.SUSPENDED) {

                generateInvoiceForSubscription(sub);
            }
        }


        return sub;
    }



    public void activateSubscription(Integer subscriptionId) {


        Subscription sub =
                subscriptionRepository.findById(subscriptionId);


        LocalDate today = LocalDate.now();


        sub.setStatus(SubscriptionStatus.ACTIVE);

        sub.setStartDate(today);

        sub.setEndDate(today.plusMonths(1));


        subscriptionRepository.updateSubscription(sub);
    }



    private void generateInvoiceForSubscription(Subscription sub) {


        Invoice invoice = new Invoice();


        invoice.setSubscription(sub);

        invoice.setIssueDate(LocalDate.now());

        invoice.setDueDate(
            LocalDate.now().plusDays(7)
        );


        invoice.setTotalAmount(
            BigDecimal.valueOf(sub.getMonthlyFee())
        );
        invoice.setLateFeeAmount(
            BigDecimal.ZERO
        );
        invoice.generateInvoice();
        Integer invoiceId = invoiceRepository.save(invoice);
        if (invoiceId != null) {
            invoice.setId(invoiceId);

            notificationService.notifyInvoiceCreated(
                sub.getCustomer().getId(),
                invoiceId,
                invoice.getTotalAmount(),
                invoice.getDueDate()
            );
        }
    }

}