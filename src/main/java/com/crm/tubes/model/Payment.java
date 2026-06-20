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

    public Payment() {
    }

    public Payment(int id,
                   Invoice invoice,
                   LocalDate paymentDate,
                   BigDecimal amount) {

        this.id = id;
        this.invoice = invoice;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    /**
     * Method UML
     */
    public void processPayment() {
        invoice.markAsPaid();
    }

    /**
     * Method UML
     */
    public boolean verifyStatus() {
        return invoice.getStatus() != InvoiceStatus.PAID;
    }
}