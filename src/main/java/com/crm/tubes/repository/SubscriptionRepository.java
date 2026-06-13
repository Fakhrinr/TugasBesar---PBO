package com.crm.tubes.repository;

import com.crm.tubes.model.Customer;
import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SubscriptionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Subscription> subscriptionRowMapper = (rs, rowNum) -> {

        // Build the Customer object from the JOIN result
        // (we JOIN subscription with customer table in our SQL)
        Customer customer = new Customer();
        customer.setId(rs.getInt("customer_id"));
        customer.setName(rs.getString("customer_name"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));

        Subscription sub = new Subscription();
        sub.setId(rs.getInt("id"));
        sub.setCustomer(customer);                          // aggregation: set Customer object
        sub.setPlanName(rs.getString("plan_name"));
        sub.setStartDate(rs.getDate("start_date").toLocalDate());
        sub.setEndDate(rs.getDate("end_date").toLocalDate());
        sub.setMonthlyFee(rs.getDouble("monthly_fee"));
        sub.setStatus(SubscriptionStatus.valueOf(rs.getString("status"))); // String → Enum

        return sub;
    };

    public List<Subscription> findAll() {
        String sql = """
                SELECT
                    s.id,
                    s.plan_name,
                    s.start_date,
                    s.end_date,
                    s.monthly_fee,
                    s.status,
                    c.id   AS customer_id,
                    c.name AS customer_name,
                    c.phone,
                    c.address
                FROM subscription s
                JOIN customer c ON s.customer_id = c.id
                ORDER BY s.id DESC
                """;

        return jdbcTemplate.query(sql, subscriptionRowMapper);
    }

 
    public Subscription findById(int id) {
        String sql = """
                SELECT
                    s.id,
                    s.plan_name,
                    s.start_date,
                    s.end_date,
                    s.monthly_fee,
                    s.status,
                    c.id   AS customer_id,
                    c.name AS customer_name,
                    c.phone,
                    c.address
                FROM subscription s
                JOIN customer c ON s.customer_id = c.id
                WHERE s.id = ?
                """;

        return jdbcTemplate.queryForObject(sql, subscriptionRowMapper, id);
    }


    public Subscription findActiveByCustomerId(int customerId) {
        String sql = """
                SELECT
                    s.id,
                    s.plan_name,
                    s.start_date,
                    s.end_date,
                    s.monthly_fee,
                    s.status,
                    c.id   AS customer_id,
                    c.name AS customer_name,
                    c.phone,
                    c.address
                FROM subscription s
                JOIN customer c ON s.customer_id = c.id
                WHERE s.customer_id = ?
                AND   s.status IN ('ACTIVE', 'GRACE')
                ORDER BY s.start_date DESC
                LIMIT 1
                """;

        return jdbcTemplate.queryForObject(sql, subscriptionRowMapper, customerId);
    }

    public void updateStatus(int id, SubscriptionStatus status) {
        String sql = "UPDATE subscription SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), id);
    }
}
