package com.crm.tubes.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Payment {

    private int id;
    private Invoice invoice;

    private LocalDate paymentDate;

    private BigDecimal amount;

    private PaymentStatus status;

    public Payment() {
    }

    public Payment(int id,
                   Invoice invoice,
                   LocalDate paymentDate,
                   BigDecimal amount,
                   PaymentStatus status) {

        this.id = id;
        this.invoice = invoice;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.status = status;
    }

    public void processPayment() {

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            status = PaymentStatus.SUCCESS;
        } else {
            status = PaymentStatus.FAILED;
        }
    }

    public boolean verifyStatus() {
        return status == PaymentStatus.SUCCESS;
    }
}