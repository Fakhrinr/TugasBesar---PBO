package com.crm.tubes.service;

import com.crm.tubes.model.InvoiceStatus;
import com.crm.tubes.model.Payment;
import com.crm.tubes.repository.InvoiceRepository;
import com.crm.tubes.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Process payment
     */
    public void processPayment(Payment payment) {

        payment.processPayment();

        paymentRepository.save(payment);

        if (payment.verifyStatus()) {

            invoiceRepository.updateStatus(
                    payment.getInvoice().getId(),
                    InvoiceStatus.PAID
            );
        }
    }

    /**
     * Get all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payments by invoice
     */
    public List<Payment> getPaymentsByInvoice(int invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }
}