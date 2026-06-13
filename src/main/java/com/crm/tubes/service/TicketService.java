package com.crm.tubes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.tubes.model.TicketModel;
import com.crm.tubes.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<TicketModel> getAllTickets() {
        return ticketRepository.findAll();
    }

    public TicketModel getTicketById(int id) {
        return ticketRepository.findById(id);
    }

    public void createTicket(TicketModel ticket) {

        if (ticket.getStatus() == null || ticket.getStatus().isEmpty()) {
            ticket.setStatus("OPEN");
        }

        if (ticket.getPriority() == null || ticket.getPriority().isEmpty()) {
            ticket.setPriority("MEDIUM");
        }

        ticketRepository.save(ticket);
    }

    public void updateTicket(TicketModel ticket) {
        ticketRepository.update(ticket);
    }

    public void deleteTicket(int id) {
        ticketRepository.delete(id);
    }

    public void assignTechnician(int ticketId, int technicianId) {

        TicketModel ticket = ticketRepository.findById(ticketId);

        ticket.setTechnicianId(technicianId);
        ticket.setStatus("IN_PROGRESS");

        ticketRepository.update(ticket);
    }

    public void resolveTicket(int ticketId) {

        TicketModel ticket = ticketRepository.findById(ticketId);

        ticket.setStatus("RESOLVED");

        ticketRepository.update(ticket);
    }

    public void closeTicket(int ticketId) {

        TicketModel ticket = ticketRepository.findById(ticketId);

        ticket.setStatus("CLOSED");

        ticketRepository.update(ticket);
    }
}