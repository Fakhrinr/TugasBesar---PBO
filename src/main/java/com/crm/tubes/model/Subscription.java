package com.crm.tubes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    // Primary key — ID unik tiap subscription di database
    private Integer id;

    // AGGREGATION: Subscription punya CustomerModel
    // Sesuai class diagram: Subscription <>── Customer
    private CustomerModel customer;

    // Nama paket internet, e.g. "Starter Plan", "Pro Plan"
    private String planName;

    // Tanggal mulai berlangganan
    private LocalDate startDate;

    // Tanggal berakhir berlangganan
    private LocalDate endDate;

    // Biaya per bulan, e.g. 29.99
    private Double monthlyFee;

    // Status: ACTIVE, GRACE, atau SUSPENDED
    private SubscriptionStatus status;

    // ── Business Logic Methods ────────────────────────────────
    // Getter/setter sudah otomatis dari @Data
    // Kita hanya perlu tambah method logic bisnis

    /**
     * activate() — ubah status jadi ACTIVE.
     * Dipanggil ketika pembayaran customer dikonfirmasi.
     */
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }

    /**
     * suspend() — ubah status jadi SUSPENDED.
     * Dipanggil ketika subscription expired atau belum bayar.
     */
    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
    }

    /**
     * setGracePeriod() — ubah status jadi GRACE.
     * Dipanggil ketika customer dalam 7 hari menjelang expired.
     */
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
            // Udah lewat tanggal expired → putus akses
            this.status = SubscriptionStatus.SUSPENDED;

        } else if (!today.isBefore(endDate.minusDays(7))) {
            // Dalam 7 hari mau expired → kasih warning
            this.status = SubscriptionStatus.GRACE;

        } else {
            // Masih jauh dari expired → normal
            this.status = SubscriptionStatus.ACTIVE;
        }
    }
}