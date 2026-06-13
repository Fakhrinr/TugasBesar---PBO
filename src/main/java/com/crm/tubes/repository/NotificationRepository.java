package com.crm.tubes.repository;

import com.crm.tubes.model.NotificationModel;
import com.crm.tubes.model.NotificationType;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<NotificationModel> notificationRowMapper =
            (rs, rowNum) -> new NotificationModel(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    NotificationType.valueOf(rs.getString("type")),
                    rs.getString("message"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );

    public int save(NotificationModel notification) {
        String sql = """
                INSERT INTO notification (
                    user_id,
                    type,
                    message
                ) VALUES (?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                notification.getUserId(),
                notification.getType().name(),
                notification.getMessage()
        );
    }

    public Optional<NotificationModel> findById(
            Integer notificationId
    ) {
        String sql = """
                SELECT
                    id,
                    user_id,
                    type,
                    message,
                    is_read,
                    created_at
                FROM notification
                WHERE id = ?
                """;

        try {
            NotificationModel notification =
                    jdbcTemplate.queryForObject(
                            sql,
                            notificationRowMapper,
                            notificationId
                    );

            return Optional.ofNullable(notification);

        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<NotificationModel> findByUserId(
            Integer userId
    ) {
        String sql = """
                SELECT
                    id,
                    user_id,
                    type,
                    message,
                    is_read,
                    created_at
                FROM notification
                WHERE user_id = ?
                ORDER BY created_at DESC, id DESC
                """;

        return jdbcTemplate.query(
                sql,
                notificationRowMapper,
                userId
        );
    }

    public List<NotificationModel> findUnreadByUserId(
            Integer userId
    ) {
        String sql = """
                SELECT
                    id,
                    user_id,
                    type,
                    message,
                    is_read,
                    created_at
                FROM notification
                WHERE user_id = ?
                  AND is_read = FALSE
                ORDER BY created_at DESC, id DESC
                """;

        return jdbcTemplate.query(
                sql,
                notificationRowMapper,
                userId
        );
    }

    public int countUnreadByUserId(
            Integer userId
    ) {
        String sql = """
                SELECT COUNT(*)
                FROM notification
                WHERE user_id = ?
                  AND is_read = FALSE
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId
        );

        return count == null ? 0 : count;
    }

    public List<NotificationModel> findByUserIdAndType(
            Integer userId,
            NotificationType type
    ) {
        String sql = """
                SELECT
                    id,
                    user_id,
                    type,
                    message,
                    is_read,
                    created_at
                FROM notification
                WHERE user_id = ?
                  AND type = ?
                ORDER BY created_at DESC, id DESC
                """;

        return jdbcTemplate.query(
                sql,
                notificationRowMapper,
                userId,
                type.name()
        );
    }

    public int markAsRead(
            Integer notificationId
    ) {
        String sql = """
                UPDATE notification
                SET is_read = TRUE
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                notificationId
        );
    }

    public int markAllAsRead(
            Integer userId
    ) {
        String sql = """
                UPDATE notification
                SET is_read = TRUE
                WHERE user_id = ?
                  AND is_read = FALSE
                """;

        return jdbcTemplate.update(
                sql,
                userId
        );
    }

    public int deleteById(
            Integer notificationId
    ) {
        String sql = """
                DELETE FROM notification
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                notificationId
        );
    }
}