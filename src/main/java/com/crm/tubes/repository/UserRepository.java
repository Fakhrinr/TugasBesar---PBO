package com.crm.tubes.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.StaffModel;
import com.crm.tubes.model.TechnicianModel;
import com.crm.tubes.model.UserModel;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    // ── INSERT user + subtype ────────────────────────────────

    public Integer registerUser(UserModel user) {

        Integer userId = saveUser(user);

        if (user instanceof CustomerModel c) {
            saveCustomer(userId, c.getAddress(), c.getPhone());

        } else if (user instanceof TechnicianModel t) {
            saveTechnician(userId, t.getArea());

        } else if (user instanceof StaffModel s) {
            saveStaff(userId, s.getEmployeeId());
        }

        return userId;
    }

    private Integer saveUser(UserModel user) {

        String sql = "INSERT INTO user (name,email,password,role,status) VALUES (?,?,?,?,?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.setBoolean(5, Boolean.TRUE.equals(user.getStatus()));
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new RuntimeException("Gagal mendapatkan ID user");
        return key.intValue();
    }

    public void saveCustomer(Integer userId, String address, String phone) {
        jdbcTemplate.update(
            "INSERT INTO customer (user_id,address,phone) VALUES (?,?,?)",
            userId, address, phone);
    }

    public void saveStaff(Integer userId, String employeeId) {
        jdbcTemplate.update(
            "INSERT INTO staff (user_id,employee_id) VALUES (?,?)",
            userId, employeeId);
    }

    public void saveTechnician(Integer userId, String area) {
        jdbcTemplate.update(
            "INSERT INTO technician (user_id,area) VALUES (?,?)",
            userId, area);
    }

    // ── SELECT ───────────────────────────────────────────────

    public UserModel findByEmail(String email) {

        String sql = "SELECT * FROM user WHERE email = ?";

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return null;

            UserModel u = new UserModel();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(UserModel.Role.valueOf(rs.getString("role")));
            u.setStatus(rs.getBoolean("status"));
            return u;
        }, email);
    }

    /** Returns customer.id for a given user.id, or null if not a customer. */
    public Integer findCustomerIdByUserId(Integer userId) {

        return jdbcTemplate.query(
            "SELECT id FROM customer WHERE user_id = ?",
            rs -> rs.next() ? rs.getInt("id") : null,
            userId);
    }

    // ── UPDATE ───────────────────────────────────────────────

    public void updatePassword(Integer userId, String newPassword) {
        jdbcTemplate.update(
            "UPDATE user SET password = ? WHERE id = ?",
            newPassword, userId);
    }

    public long countByRole(UserModel.Role role) {
    Long result = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM user WHERE role = ?",
        Long.class,
        role.name()
    );
    return result == null ? 0 : result;
    }

    public List<UserModel> findByRole(UserModel.Role role) {
    return jdbcTemplate.query(
        "SELECT * FROM user WHERE role = ?",
        (rs, rowNum) -> {
            UserModel u = new UserModel();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(UserModel.Role.valueOf(rs.getString("role")));
            u.setStatus(rs.getBoolean("status"));
            return u;
        },
        role.name()
    );
    }

    public void updateStatus(Integer userId, Boolean status) {
        jdbcTemplate.update(
            "UPDATE user SET status = ? WHERE id = ?",
            status, userId);
    }
}
