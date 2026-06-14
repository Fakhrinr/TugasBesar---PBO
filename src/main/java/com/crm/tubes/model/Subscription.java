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


    public void setGracePeriod() {
        this.status = SubscriptionStatus.GRACE;
    }

    /**
     * checkStatus() — otomatis update status berdasarkan tanggal hari ini.
     *
     * Logic (sesuai dokumen section 2.2.2):
     *   - Hari ini SETELAH endDate      → SUSPENDED (expired)
     *   - Hari ini DALAM 7 hari sebelum endDate → GRACE (mau expired)
     *   - Selain itu                    → ACTIVE (normal)
     */
    public void checkStatus() {
        LocalDate today = LocalDate.now();

        if (today.isAfter(endDate)) {
            this.status = SubscriptionStatus.SUSPENDED;

        } else if (!today.isBefore(endDate.minusDays(7))) {
            // Dalam  hari mau expired → kasih warning
            this.status = SubscriptionStatus.GRACE;

        } else {
            // Masih jauh dari expired → normal
            this.status = SubscriptionStatus.ACTIVE;
        }
    }
}
