package com.crm.tubes.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.crm.tubes.model.TicketModel;

@Repository
public class TicketRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TicketModel> ticketRowMapper = (rs, rowNum) ->
            new TicketModel(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    (Integer) rs.getObject("technician_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
            );

    public List<TicketModel> findAll() {

        String sql = "SELECT * FROM ticket";

        return jdbcTemplate.query(sql, ticketRowMapper);
    }

    public List<TicketModel> findByTechnicianId(int technicianId) {

        String sql = """
                SELECT *
                FROM ticket
                WHERE technician_id = ?
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(
                sql,
                ticketRowMapper,
                technicianId
        );
    }

    public TicketModel findById(int id) {

    String sql = "SELECT * FROM ticket WHERE id = ?";

    return jdbcTemplate.queryForObject(
            sql,
            ticketRowMapper,
            id
    );
}

    public Integer save(TicketModel ticket) {

        String sql =
                """
                INSERT INTO ticket
                (customer_id, technician_id, title, description, priority, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

       KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setInt(1, ticket.getCustomerId());

            if (ticket.getTechnicianId() == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, ticket.getTechnicianId());
            }

            statement.setString(3, ticket.getTitle());
            statement.setString(4, ticket.getDescription());
            statement.setString(5, ticket.getPriority());
            statement.setString(6, ticket.getStatus());

            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? null : key.intValue();
    }

    public void update(TicketModel ticket) {

        String sql =
                """
                UPDATE ticket
                SET technician_id=?,
                    title=?,
                    description=?,
                    priority=?,
                    status=?
                WHERE id=?
                """;

        jdbcTemplate.update(
                sql,
                ticket.getTechnicianId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getId()
        );
    }

    public void delete(int id) {

    String sql = "DELETE FROM ticket WHERE id=?";

    jdbcTemplate.update(sql, id);
}

public void assignTechnician(
        int ticketId,
        int technicianId
) {

    String sql =
            """
            UPDATE ticket
            SET technician_id=?,
                status='IN_PROGRESS'
            WHERE id=?
            """;

    jdbcTemplate.update(
            sql,
            technicianId,
            ticketId
    );
}

public void updateStatus(
        int ticketId,
        String status
) {

    String sql =
            """
            UPDATE ticket
            SET status=?
            WHERE id=?
            """;

    jdbcTemplate.update(
            sql,
            status,
            ticketId
    );
}

public List<TicketModel> findByCustomerId(int customerId) {

    String sql = """
        SELECT *
        FROM ticket
        WHERE customer_id = ?
        ORDER BY created_at DESC
        """;

    return jdbcTemplate.query(
        sql,
        ticketRowMapper,
        customerId
    );
}

}
