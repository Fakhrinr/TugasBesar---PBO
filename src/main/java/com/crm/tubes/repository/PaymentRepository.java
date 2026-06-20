package com.crm.tubes.repository;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Save payment
     */
    public void save(Payment payment) {

        String sql = """
                INSERT INTO payment
                (
                    invoice_id,
                    payment_date,
                    amount
                )
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                payment.getInvoice().getId(),
                Date.valueOf(payment.getPaymentDate()),
                payment.getAmount()
        );
    }

    /**
     * Get all payments
     */
    public List<Payment> findAll() {

        String sql = """
                SELECT *
                FROM payment
                ORDER BY id DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Invoice invoice = new Invoice();
                    invoice.setId(
                            rs.getInt("invoice_id")
                    );

                    Payment payment = new Payment();

                    payment.setId(
                            rs.getInt("id")
                    );

                    payment.setInvoice(
                            invoice
                    );

                    payment.setPaymentDate(
                            rs.getDate("payment_date")
                                    .toLocalDate()
                    );

                    payment.setAmount(
                            rs.getBigDecimal("amount")
                    );

                    return payment;
                }
        );
    }

    /**
     * Find payments by invoice
     */
    public List<Payment> findByInvoiceId(
            int invoiceId
    ) {

        String sql = """
                SELECT *
                FROM payment
                WHERE invoice_id = ?
                ORDER BY payment_date DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Invoice invoice = new Invoice();
                    invoice.setId(
                            rs.getInt("invoice_id")
                    );

                    Payment payment = new Payment();

                    payment.setId(
                            rs.getInt("id")
                    );

                    payment.setInvoice(
                            invoice
                    );

                    payment.setPaymentDate(
                            rs.getDate("payment_date")
                                    .toLocalDate()
                    );

                    payment.setAmount(
                            rs.getBigDecimal("amount")
                    );

                    return payment;
                },
                invoiceId
        );
    }
}