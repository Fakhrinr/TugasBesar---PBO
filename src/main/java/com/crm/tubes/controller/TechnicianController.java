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
import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.AuthService;
import com.crm.tubes.service.DashboardService;
import com.crm.tubes.service.TicketService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    /*
     * DASHBOARD
     * Sebenernya sudah dihandle DashboardController,
     * ini fallback kalau ada yang akses /technician/dashboard langsung
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UserModel user = authService.getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("data", dashboardService.getTeknisiData(user));
        return "dashboard/teknisi";
    }

    /*
     * MY TICKETS
     */
    @GetMapping("/my-tickets")
    public String myTickets(HttpSession session, Model model) {
        UserModel user = authService.getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute(
                "tickets",
                ticketService.getTicketsByTechnicianId(user.getId())
        );
        return "my-tickets";
    }

    /*
     * TICKET DETAIL
     */
    @GetMapping("/my-tickets/{id:[0-9]+}")
    public String ticketDetail(
            @PathVariable int id,
            HttpSession session,
            Model model
    ) {
        UserModel user = authService.getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("ticket", ticketService.getTicketById(id));
        return "ticket-detail";
    }

    @PostMapping("/my-tickets/{id:[0-9]+}/start")
    public String startTicket(
            @PathVariable int id,
            HttpSession session
    ) {
        UserModel user = authService.getLoggedUser(session);
        if (user == null) return "redirect:/login";

        ticketService.assignTechnician(id, user.getId());
        return "redirect:/technician/my-tickets/" + id;
    }

    @PostMapping("/my-tickets/{id:[0-9]+}/resolve")
    public String resolveTicket(
            @PathVariable int id,
            HttpSession session
    ) {
        UserModel user = authService.getLoggedUser(session);
        if (user == null) return "redirect:/login";

        ticketService.resolveTicket(id);
        return "redirect:/technician/my-tickets/" + id;
    }
}