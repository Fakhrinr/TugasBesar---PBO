package com.crm.tubes.repository;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SubscriptionRepository — ngomong sama database MySQL.
 *
 * @Repository         → Spring tau ini class database
 * @RequiredArgsConstructor → Lombok inject JdbcTemplate otomatis
 */
@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {

    // JdbcTemplate diinjek Lombok lewat constructor
    private final JdbcTemplate jdbcTemplate;

    // ── RowMapper ─────────────────────────────────────────────────────────
    /**
     * Ngubah satu baris dari database → satu object Subscription.
     * Pake CustomerModel (punya Tiara) untuk data customer nya.
     */
    private final RowMapper<Subscription> subscriptionRowMapper = (rs, rowNum) -> {

        // Build CustomerModel dari hasil JOIN dengan tabel user & customer
        CustomerModel customer = new CustomerModel();
        customer.setIdUser(rs.getInt("customer_id"));
        customer.setName(rs.getString("customer_name"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));

        // Build Subscription object
        Subscription sub = new Subscription();
        sub.setId(rs.getInt("id"));
        sub.setCustomer(customer);
        sub.setPlanName(rs.getString("plan_name"));
        sub.setStartDate(rs.getDate("start_date").toLocalDate());
        sub.setEndDate(rs.getDate("end_date").toLocalDate());
        sub.setMonthlyFee(rs.getDouble("monthly_fee"));
        sub.setStatus(SubscriptionStatus.valueOf(rs.getString("status")));

        return sub;
    };

    // ── Base SQL ──────────────────────────────────────────────────────────
    // SQL dasar yang dipake semua query (biar ga nulis ulang)
    private static final String BASE_SQL = """
            SELECT
                s.id,
                s.plan_name,
                s.start_date,
                s.end_date,
                s.monthly_fee,
                s.status,
                c.id       AS customer_id,
                u.name     AS customer_name,
                c.phone,
                c.address
            FROM subscription s
            JOIN customer c ON s.customer_id = c.id
            JOIN user u     ON c.user_id = u.id
            """;

    // ── Queries ───────────────────────────────────────────────────────────

    /**
     * findAll() — ambil SEMUA subscription dari database.
     * Dipakai untuk Admin/Teknisi view.
     */
    public List<Subscription> findAll() {
        String sql = BASE_SQL + " ORDER BY s.id DESC";
        return jdbcTemplate.query(sql, subscriptionRowMapper);
    }

    /**
     * findByCustomerId() — ambil semua subscription milik 1 customer.
     * Dipakai untuk history table di Customer view.
     *
     * @param customerId ID customer yang lagi login
     */
    public List<Subscription> findByCustomerId(int customerId) {
        String sql = BASE_SQL + " WHERE c.id = ? ORDER BY s.start_date DESC";
        return jdbcTemplate.query(sql, subscriptionRowMapper, customerId);
    }

    /**
     * findActiveByCustomerId() — ambil subscription AKTIF milik 1 customer.
     * Dipakai untuk card plan aktif di Customer view.
     *
     * @param customerId ID customer yang lagi login
     */
    public Subscription findActiveByCustomerId(int customerId) {
        String sql = BASE_SQL + """
                 WHERE c.id = ?
                 AND s.status IN ('ACTIVE', 'GRACE')
                 ORDER BY s.start_date DESC
                 LIMIT 1
                """;
        return jdbcTemplate.queryForObject(sql, subscriptionRowMapper, customerId);
    }

    /**
     * updateStatus() — update kolom status di database.
     * Dipanggil Service setelah checkStatus() jalan.
     *
     * @param id     ID subscription yang mau diupdate
     * @param status status baru (ACTIVE/GRACE/SUSPENDED)
     */
    public void updateStatus(int id, SubscriptionStatus status) {
        String sql = "UPDATE subscription SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), id);
    }
}