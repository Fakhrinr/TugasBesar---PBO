package com.crm.tubes.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Invoice {

    private int id;
    private Subscription subscription;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private BigDecimal totalAmount;
    private BigDecimal lateFeeAmount;

    private InvoiceStatus status;

    public Invoice() {
    }

    public Invoice(int id,
                   Subscription subscription,
                   LocalDate issueDate,
                   LocalDate dueDate,
                   BigDecimal totalAmount,
                   BigDecimal lateFeeAmount,
                   InvoiceStatus status) {

        this.id = id;
        this.subscription = subscription;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.lateFeeAmount = lateFeeAmount;
        this.status = status;
    }

    public void generateInvoice() {
        this.status = InvoiceStatus.OPEN;
    }

    public BigDecimal calculateTotal() {
        return totalAmount.add(lateFeeAmount);
    }

    public void applyLateFee(BigDecimal fee) {
        this.lateFeeAmount = this.lateFeeAmount.add(fee);
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
    }

    public void checkStatus() {

        if (status != InvoiceStatus.PAID &&
                LocalDate.now().isAfter(dueDate)) {

            status = InvoiceStatus.OVERDUE;
        }
    }
}