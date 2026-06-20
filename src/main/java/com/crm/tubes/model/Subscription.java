package com.crm.tubes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private Integer id;
    private CustomerModel customer;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyFee;
    private SubscriptionStatus status;

    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
    }
\
    public void setGracePeriod() {
        this.status = SubscriptionStatus.GRACE;
    }
\
    public void checkStatus() {
        LocalDate today = LocalDate.now();

        if (today.isAfter(endDate)) {
\            this.status = SubscriptionStatus.SUSPENDED;

        } else if (!today.isBefore(endDate.minusDays(7))) {
            this.status = SubscriptionStatus.GRACE;

        } else {
            this.status = SubscriptionStatus.ACTIVE;
        }
    }

    public void renewFromNow() {
    LocalDate today = LocalDate.now();

    this.status = SubscriptionStatus.ACTIVE;
    this.startDate = today;
    this.endDate = today.plusMonths(1);
    }
}