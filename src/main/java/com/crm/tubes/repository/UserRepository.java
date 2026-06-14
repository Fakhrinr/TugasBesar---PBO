package com.crm.tubes.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.StaffModel;
import com.crm.tubes.model.UserModel;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public Integer registerUser(UserModel user) {

        Integer userId = save(user);

        if (user instanceof CustomerModel customer) {

            saveCustomer(
                    userId,
                    customer.getAddress(),
                    customer.getPhone()
            );

        } else if (user instanceof StaffModel staff) {

            saveStaff(
                    userId,
                    staff.getEmployeeId()
            );
        }

        return userId;
    }

    public Integer save(UserModel user) {

        String sql = """
                INSERT INTO user
                (name,email,password,role,status)
                VALUES (?,?,?,?,?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setString(
                    1,
                    user.getName()
            );

            ps.setString(
                    2,
                    user.getEmail()
            );

            ps.setString(
                    3,
                    user.getPassword()
            );

            ps.setString(
                    4,
                    user.getRole().name()
            );

            ps.setBoolean(
                    5,
                    user.getStatus()
            );

            return ps;

        }, keyHolder);

        Number key =
                keyHolder.getKey();

        if (key == null) {

            throw new RuntimeException(
                    "Gagal mendapatkan ID user"
            );
        }

        return key.intValue();
    }

    public void saveCustomer(
            Integer userId,
            String address,
            String phone
    ) {

        String sql = """
                INSERT INTO customer
                (user_id,address,phone)
                VALUES (?,?,?)
                """;

        jdbcTemplate.update(
                sql,
                userId,
                address,
                phone
        );
    }

    public void saveStaff(
            Integer userId,
            String employeeId
    ) {

        String sql = """
                INSERT INTO staff
                (user_id,employee_id)
                VALUES (?,?)
                """;

        jdbcTemplate.update(
                sql,
                userId,
                employeeId
        );
    }

    public UserModel findByEmail(
            String email
    ) {

        String sql = """
                SELECT *
                FROM user
                WHERE email = ?
                """;

        return jdbcTemplate.query(
                sql,
                rs -> {

                    if (!rs.next()) {
                        return null;
                    }

                    UserModel user =
                            new UserModel();

                    user.setIdUser(
                            rs.getInt("id")
                    );

                    user.setName(
                            rs.getString("name")
                    );

                    user.setEmail(
                            rs.getString("email")
                    );

                    user.setPassword(
                            rs.getString("password")
                    );

                    user.setRole(
                            UserModel.Role.valueOf(
                                    rs.getString("role")
                            )
                    );

                    user.setStatus(
                            rs.getBoolean("status")
                    );

                    return user;
                },
                email
        );
    }

    public Integer findCustomerIdByUserId(
        Integer userId
        ) {

        String sql = """
            SELECT id
            FROM customer
            WHERE user_id = ?
            """;

        return jdbcTemplate.query(
            sql,
            rs -> rs.next()
                    ? rs.getInt("id")
                    : null,
            userId
        );
        }

    public void updatePassword(
            String email,
            String password
    ) {

        String sql = """
                UPDATE user
                SET password = ?
                WHERE email = ?
                """;

        jdbcTemplate.update(
                sql,
                password,
                email
        );
    }

    public void updateStatus(
            Integer userId,
            Boolean status
    ) {

        String sql = """
                UPDATE user
                SET status = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                status,
                userId
        );
    }
}