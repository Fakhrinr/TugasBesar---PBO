package com.crm.tubes.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.crm.tubes.model.TicketModel;

@Repository
public class TicketRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TicketModel> findAll() {

        String sql = "SELECT * FROM ticket";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new TicketModel(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        (Integer) rs.getObject("technician_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                )
        );
    }
    

    public TicketModel findById(int id) {

    String sql = "SELECT * FROM ticket WHERE id = ?";

    return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new TicketModel(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    (Integer) rs.getObject("technician_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
            ),
            id
    );
}

    public void save(TicketModel ticket) {

        String sql =
                """
                INSERT INTO ticket
                (customer_id, technician_id, title, description, priority, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                ticket.getCustomerId(),
                ticket.getTechnicianId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus()
        );
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

}