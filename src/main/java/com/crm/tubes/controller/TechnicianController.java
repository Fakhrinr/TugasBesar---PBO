package com.crm.tubes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.crm.tubes.model.TicketModel;
import com.crm.tubes.service.TicketService;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

@Autowired
private TicketService ticketService;

/*
 * SEMENTARA
 * nanti ganti dari session/login
 */
private final int TECHNICIAN_ID = 1;

/*
 * DASHBOARD
 */
@GetMapping("/dashboard")
public String dashboard(Model model) {

    List<TicketModel> tickets =
            ticketService
                    .getTicketsByTechnicianId(TECHNICIAN_ID);

    model.addAttribute(
            "assignedTicketCount",
            tickets.size()
    );

    model.addAttribute(
            "inProgressCount",
            tickets.stream()
                    .filter(t ->
                            "IN_PROGRESS".equals(t.getStatus()))
                    .count()
    );

    model.addAttribute(
            "resolvedCount",
            tickets.stream()
                    .filter(t ->
                            "RESOLVED".equals(t.getStatus()))
                    .count()
    );

    model.addAttribute(
            "recentTickets",
            tickets
    );

    return "technician/dashboard";
}

/*
 * MY TICKETS
 */
@GetMapping("/my-tickets")
public String myTickets(Model model) {

    List<TicketModel> tickets =
            ticketService
                    .getTicketsByTechnicianId(TECHNICIAN_ID);

    model.addAttribute(
            "tickets",
            tickets
    );

    return "technician/my-tickets";
}

/*
 * TICKET DETAIL
 */
@GetMapping("/my-tickets/{id:[0-9]+}")
public String ticketDetail(
        @PathVariable int id,
        Model model
) {

    TicketModel ticket =
            ticketService
                    .getTicketById(id);

    model.addAttribute(
            "ticket",
            ticket
    );

    return "technician/ticket-detail";
}

@PostMapping("/my-tickets/{id:[0-9]+}/start")
public String startTicket(
        @PathVariable int id
) {

    ticketService.assignTechnician(
            id,
            TECHNICIAN_ID
    );

    return "redirect:/technician/my-tickets/" + id;
}

@PostMapping("/my-tickets/{id:[0-9]+}/resolve")
public String resolveTicket(
        @PathVariable int id
) {

    ticketService.resolveTicket(id);

    return "redirect:/technician/my-tickets/" + id;
}

}
