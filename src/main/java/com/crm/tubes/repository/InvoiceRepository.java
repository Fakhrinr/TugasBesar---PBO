package com.crm.tubes.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.InvoiceStatus;
import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.SubscriptionStatus;

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
                SELECT
                    i.*,

                    s.plan_name,
                    s.start_date,
                    s.end_date,
                    s.monthly_fee,
                    s.status AS subscription_status,

                    c.id AS customer_id,
                    c.user_id,
                    c.phone,
                    c.address,

                    u.name

                FROM invoice i

                JOIN subscription s
                    ON i.subscription_id = s.id

                JOIN customer c
                    ON s.customer_id = c.id

                JOIN user u
                    ON c.user_id = u.id

                WHERE i.id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    CustomerModel customer = new CustomerModel();

                        customer.setCustomerId(
                                rs.getInt("customer_id")
                        );

                        customer.setId(
                                rs.getInt("user_id")
                        );

                       customer.setName(
                                rs.getString("name")
                        );

                        customer.setPhone(
                                rs.getString("phone")
                        );

                        customer.setAddress(
                                 rs.getString("address")
                        ); 
                        
                    Subscription subscription = new Subscription();

                    subscription.setId(
                            rs.getInt("subscription_id")
                    );

                    subscription.setCustomer(
                            customer
                    );

                    subscription.setPlanName(
                            rs.getString("plan_name")
                    );

                    subscription.setStartDate(
                            rs.getDate("start_date")
                                    .toLocalDate()
                    );

                    subscription.setEndDate(
                            rs.getDate("end_date")
                                    .toLocalDate()
                    );

                    subscription.setMonthlyFee(
                            rs.getDouble("monthly_fee")
                    );

                    subscription.setStatus(
                            SubscriptionStatus.valueOf(
                                    rs.getString("subscription_status")
                            )
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
                SELECT
                    i.*,

                    s.plan_name,
                    s.start_date,
                    s.end_date,
                    s.monthly_fee,
                    s.status AS subscription_status,

                    c.id AS customer_id,
                    c.user_id,
                    c.phone,
                    c.address,

                    u.name

                FROM invoice i

                JOIN subscription s
                    ON i.subscription_id = s.id

                JOIN customer c
                    ON s.customer_id = c.id

                JOIN user u
                    ON c.user_id = u.id

                ORDER BY i.id DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    CustomerModel customer = new CustomerModel();

                        customer.setCustomerId(
                                rs.getInt("customer_id")
                        );

                        customer.setId(
                                rs.getInt("user_id")
                        );

                       customer.setName(
                                rs.getString("name")
                        );

                        customer.setPhone(
                                rs.getString("phone")
                        );

                        customer.setAddress(
                                 rs.getString("address")
                        );     

                    Subscription subscription = new Subscription();

                    subscription.setId(
                            rs.getInt("subscription_id")
                    );

                    subscription.setCustomer(
                            customer
                    );

                    subscription.setPlanName(
                            rs.getString("plan_name")
                    );

                    subscription.setStartDate(
                            rs.getDate("start_date")
                                    .toLocalDate()
                    );

                    subscription.setEndDate(
                            rs.getDate("end_date")
                                    .toLocalDate()
                    );

                    subscription.setMonthlyFee(
                            rs.getDouble("monthly_fee")
                    );

                    subscription.setStatus(
                            SubscriptionStatus.valueOf(
                                    rs.getString("subscription_status")
                            )
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