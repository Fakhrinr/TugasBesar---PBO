package com.crm.tubes.repository;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.InvoiceStatus;
import com.crm.tubes.model.Subscription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class InvoiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public InvoiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Save new invoice
     */
    public void save(Invoice invoice) {

        String sql = """
                INSERT INTO invoice
                (
                    subscription_id,
                    issue_date,
                    due_date,
                    total_amount,
                    late_fee_amount,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                invoice.getSubscription().getId(),
                Date.valueOf(invoice.getIssueDate()),
                Date.valueOf(invoice.getDueDate()),
                invoice.getTotalAmount(),
                invoice.getLateFeeAmount(),
                invoice.getStatus().name()
        );
    }

    /**
     * Get invoice by ID
     */
    public Invoice findById(int id) {

        String sql = """
                SELECT *
                FROM invoice
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    Subscription subscription = new Subscription();
                    subscription.setId(
                            rs.getInt("subscription_id")
                    );

                    Invoice invoice = new Invoice();

                    invoice.setId(
                            rs.getInt("id")
                    );

                    invoice.setSubscription(
                            subscription
                    );

                    invoice.setIssueDate(
                            rs.getDate("issue_date")
                                    .toLocalDate()
                    );

                    invoice.setDueDate(
                            rs.getDate("due_date")
                                    .toLocalDate()
                    );

                    invoice.setTotalAmount(
                            rs.getBigDecimal("total_amount")
                    );

                    invoice.setLateFeeAmount(
                            rs.getBigDecimal("late_fee_amount")
                    );

                    invoice.setStatus(
                            InvoiceStatus.valueOf(
                                    rs.getString("status")
                            )
                    );

                    return invoice;
                },
                id
        );
    }

    /**
     * Get all invoices
     */
    public List<Invoice> findAll() {

        String sql = """
                SELECT *
                FROM invoice
                ORDER BY id DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Subscription subscription = new Subscription();
                    subscription.setId(
                            rs.getInt("subscription_id")
                    );

                    Invoice invoice = new Invoice();

                    invoice.setId(
                            rs.getInt("id")
                    );

                    invoice.setSubscription(
                            subscription
                    );

                    invoice.setIssueDate(
                            rs.getDate("issue_date")
                                    .toLocalDate()
                    );

                    invoice.setDueDate(
                            rs.getDate("due_date")
                                    .toLocalDate()
                    );

                    invoice.setTotalAmount(
                            rs.getBigDecimal("total_amount")
                    );

                    invoice.setLateFeeAmount(
                            rs.getBigDecimal("late_fee_amount")
                    );

                    invoice.setStatus(
                            InvoiceStatus.valueOf(
                                    rs.getString("status")
                            )
                    );

                    return invoice;
                }
        );
    }

    /**
     * Update invoice status
     */
    public void updateStatus(
            int invoiceId,
            InvoiceStatus status
    ) {

        String sql = """
                UPDATE invoice
                SET status = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                status.name(),
                invoiceId
        );
    }

    /**
     * Update late fee
     */
    public void updateLateFee(
            int invoiceId,
            java.math.BigDecimal lateFeeAmount
    ) {

        String sql = """
                UPDATE invoice
                SET late_fee_amount = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                lateFeeAmount,
                invoiceId
        );
    }
}