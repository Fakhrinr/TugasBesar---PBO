package com.crm.tubes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crm.tubes.model.TicketModel;
import com.crm.tubes.service.TicketService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public String getAllTickets(Model model) {

        model.addAttribute(
                "tickets",
                ticketService.getAllTickets()
        );

        return "ticket-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute(
                "ticket",
                new TicketModel()
        );

        return "ticket-form";
    }

    @PostMapping("/save")
    public String saveTicket(
            @ModelAttribute TicketModel ticket
    ) {

        ticketService.createTicket(ticket);

        return "redirect:/tickets";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable int id,
            Model model
    ) {

        model.addAttribute(
                "ticket",
                ticketService.getTicketById(id)
        );

        return "ticket-form";
    }

    @PostMapping("/update")
    public String updateTicket(
            @ModelAttribute TicketModel ticket
    ) {

        ticketService.updateTicket(ticket);

        return "redirect:/tickets";
    }

    @GetMapping("/delete/{id}")
    public String deleteTicket(
            @PathVariable int id
    ) {

        ticketService.deleteTicket(id);

        return "redirect:/tickets";
    }

    @PostMapping("/assign")
    public String assignTechnician(
            @RequestParam int ticketId,
            @RequestParam int technicianId
    ) {

        ticketService.assignTechnician(
                ticketId,
                technicianId
        );

        return "redirect:/tickets";
    }

    @PostMapping("/resolve/{id}")
    public String resolveTicket(
            @PathVariable int id
    ) {

        ticketService.resolveTicket(id);

        return "redirect:/tickets";
    }

    @PostMapping("/close/{id}")
    public String closeTicket(
            @PathVariable int id
    ) {

        ticketService.closeTicket(id);

        return "redirect:/tickets";
    }
}