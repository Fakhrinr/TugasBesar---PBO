package com.crm.tubes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crm.tubes.model.NotificationType;
import com.crm.tubes.model.TicketModel;
import com.crm.tubes.repository.TicketRepository;
import com.crm.tubes.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    
    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public List<TicketModel> getAllTickets() {
        return ticketRepository.findAll();
    }

    public TicketModel getTicketById(int id) {
        return ticketRepository.findById(id);
    }

    public List<TicketModel> getTicketsByTechnicianId(int technicianId) {
        return ticketRepository.findByTechnicianId(technicianId);
    }

    public void createTicket(TicketModel ticket) {

        if (ticket.getStatus() == null || ticket.getStatus().isEmpty()) {
            ticket.setStatus("OPEN");
        }

        if (ticket.getPriority() == null || ticket.getPriority().isEmpty()) {
            ticket.setPriority("MEDIUM");
        }

        // sementara untuk demo
        if (ticket.getTechnicianId() == null) {
            ticket.setTechnicianId(4);
        }

        Integer ticketId = ticketRepository.save(ticket);
        if (ticketId != null) {
            ticket.setId(ticketId);
            notifyCustomer(ticket, "OPEN");
        }
    }

    public void updateTicket(TicketModel ticket) {
        TicketModel existingTicket = ticketRepository.findById(ticket.getId());

        ticketRepository.update(ticket);

        if (existingTicket != null
                && ticket.getStatus() != null
                && !ticket.getStatus().equals(existingTicket.getStatus())) {
            notifyCustomer(ticket, ticket.getStatus());
        }
    }

    public void deleteTicket(int id) {
        ticketRepository.delete(id);
    }

    public void assignTechnician(int ticketId, int technicianId) {

        TicketModel ticket = ticketRepository.findById(ticketId);

        ticket.setTechnicianId(technicianId);
        ticket.setStatus("IN_PROGRESS");

        ticketRepository.update(ticket);

        notifyCustomer(ticket, "IN_PROGRESS");
        notificationService.createNotification(
                technicianId,
                NotificationType.TICKET_STATUS,
                "Ticket ID " + ticketId + " telah ditugaskan kepada Anda."
        );
    }

    public void resolveTicket(int ticketId) {

        TicketModel ticket = ticketRepository.findById(ticketId);

        ticket.setStatus("RESOLVED");

        ticketRepository.update(ticket);
        notifyCustomer(ticket, "RESOLVED");
    }

    public void closeTicket(int ticketId) {

        TicketModel ticket = ticketRepository.findById(ticketId);

        ticket.setStatus("CLOSED");

        ticketRepository.update(ticket);
          notifyCustomer(ticket, "CLOSED");
    }

    private void notifyCustomer(TicketModel ticket, String status) {
        Integer userId = userRepository.findUserIdByCustomerId(ticket.getCustomerId());

        if (userId == null) {
            return;
        }

        notificationService.notifyTicketStatus(
                userId,
                ticket.getId(),
                status
        );
    }
    
}
