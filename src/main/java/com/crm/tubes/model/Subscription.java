package com.crm.tubes.model;

import java.time.LocalDate;

/**
 * Subscription is the MODEL class.
 * A model = a blueprint that represents one row in the database table.
 *
 * This class has AGGREGATION with Customer.
 * Aggregation means: Subscription HAS-A Customer.
 * If Subscription is deleted, Customer still exists independently.
 *
 * Database table: subscription
 * Columns: id, customer_id, plan_name, start_date, end_date, monthly_fee, status
 */
public class Subscription {

    // ── Fields (variables) ────────────────────────────────────────────────────

    // Primary key — unique ID for each subscription row in DB
    private int id;

    // AGGREGATION: Subscription contains a Customer object (not just the ID)
    // This follows your class diagram where Subscription <>── Customer
    private Customer customer;

    // Name of the internet plan, e.g. "Starter Plan", "Pro Plan"
    private String planName;

    // The date the subscription started
    private LocalDate startDate;

    // The date the subscription will expire
    private LocalDate endDate;

    // How much the customer pays per month, e.g. 29.99
    private double monthlyFee;

    // Current status: ACTIVE, GRACE, or SUSPENDED (uses our enum)
    private SubscriptionStatus status;

    // ── Constructor (empty) ───────────────────────────────────────────────────

    /**
     * Empty constructor — needed by Spring/JDBC to create object before filling fields.
     */
    public Subscription() {}

    /**
     * Full constructor — used when we already have all the data ready.
     */
    public Subscription(int id, Customer customer, String planName,
                        LocalDate startDate, LocalDate endDate,
                        double monthlyFee, SubscriptionStatus status) {
        this.id          = id;
        this.customer    = customer;
        this.planName    = planName;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.monthlyFee  = monthlyFee;
        this.status      = status;
    }

    // ── Business Logic Methods (OOP) ──────────────────────────────────────────
    // These methods are the BEHAVIOR of the Subscription object.
    // This is what makes it OOP — data + behavior in one class.

    /**
     * activate() — sets status to ACTIVE.
     * Called when a customer's payment is confirmed.
     */
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }

    /**
     * suspend() — sets status to SUSPENDED.
     * Called when subscription expires or payment is not received.
     */
    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
    }

    /**
     * setGracePeriod() — sets status to GRACE.
     * Called when customer is within 7 days of expiry.
     */
    public void setGracePeriod() {
        this.status = SubscriptionStatus.GRACE;
    }

    /**
     * checkStatus() — automatically updates status based on today's date.
     *
     * Logic (from your document section 2.2.2):
     *   - If today is AFTER endDate         → SUSPENDED (expired)
     *   - If today is WITHIN 7 days of end  → GRACE (about to expire)
     *   - Otherwise                         → ACTIVE (all good)
     *
     * This method is called every time we load the subscription page
     * so the status is always up to date.
     */
    public void checkStatus() {
        LocalDate today = LocalDate.now(); // get today's date

        if (today.isAfter(endDate)) {
            // Past the end date → cut access
            this.status = SubscriptionStatus.SUSPENDED;

        } else if (!today.isBefore(endDate.minusDays(7))) {
            // Within 7 days of end date → warn the customer
            this.status = SubscriptionStatus.GRACE;

        } else {
            // Still well within the period → all good
            this.status = SubscriptionStatus.ACTIVE;
        }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    // These are standard OOP — we keep fields PRIVATE and access them
    // through public getter/setter methods (encapsulation).

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
}
