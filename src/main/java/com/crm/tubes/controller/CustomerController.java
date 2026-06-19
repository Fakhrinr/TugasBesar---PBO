package com.crm.tubes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.TicketModel;
import com.crm.tubes.service.InvoiceService;
import com.crm.tubes.service.SubscriptionService;
import com.crm.tubes.service.TicketService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

@Autowired
private SubscriptionService subscriptionService;

@Autowired
private InvoiceService invoiceService;

@Autowired
private TicketService ticketService;

/*
 * SEMENTARA
 * nanti ganti dari session/login
 */
private final int CUSTOMER_ID = 1;

/*
 * DASHBOARD
 */
@GetMapping("/dashboard")
public String dashboard(Model model) {

    // =========================
    // CUSTOMER
    // =========================
    CustomerModel customer = new CustomerModel();
    customer.setName("Demo Customer");
    customer.setEmail("customer@test.com");
    customer.setAddress("Bandung");
    customer.setPhone("08123456789");

    model.addAttribute("customer", customer);

    // =========================
    // SUBSCRIPTION
    // =========================
    Subscription activeSubscription = null;

    try {
        activeSubscription =
                subscriptionService
                        .getActiveSubscriptionByCustomer(CUSTOMER_ID);
    } catch (Exception e) {
        System.out.println("Subscription Error: " + e.getMessage());
    }

    model.addAttribute(
            "activeSubscription",
            activeSubscription
    );

    // =========================
    // TICKETS
    // =========================
    List<TicketModel> tickets;

    try {
        tickets = ticketService.getAllTickets();
    } catch (Exception e) {
        tickets = List.of();
        System.out.println("Ticket Error: " + e.getMessage());
    }

    model.addAttribute(
            "recentTickets",
            tickets
    );

    long openTicketCount =
            tickets.stream()
                    .filter(t ->
                            "OPEN".equalsIgnoreCase(
                                    t.getStatus()
                            ))
                    .count();

    model.addAttribute(
            "openTicketCount",
            openTicketCount
    );

    // =========================
    // INVOICES
    // =========================
    List<Invoice> invoices;

    try {
        invoices = invoiceService.getAllInvoices();
    } catch (Exception e) {
        invoices = List.of();
        System.out.println("Invoice Error: " + e.getMessage());
    }

    model.addAttribute(
            "recentInvoices",
            invoices
    );

    if (!invoices.isEmpty()) {

        model.addAttribute(
                "latestInvoice",
                invoices.get(0)
        );

    } else {

        model.addAttribute(
                "latestInvoice",
                null
        );
    }

    return "customer/dashboard";
}

/*
 * SERVICES
 */
@GetMapping("/services")
public String services(Model model) {

    try {

        Subscription subscription =
                subscriptionService
                        .getActiveSubscriptionByCustomer(CUSTOMER_ID);

        model.addAttribute(
                "subscription",
                subscription
        );

    } catch (Exception e) {

        e.printStackTrace();

        model.addAttribute(
                "subscription",
                null
        );
    }

    return "customer/services";
}

/*
 * BILLING
 */
@GetMapping("/billing")
public String billing(Model model) {

    List<Invoice> invoices =
            invoiceService.getAllInvoices();

    model.addAttribute(
            "invoices",
            invoices
    );

    if (!invoices.isEmpty()) {

        model.addAttribute(
                "latestInvoice",
                invoices.get(0)
        );
    }

    return "customer/billing";
}

/*
 * TICKET LIST
 */
@GetMapping("/tickets")
public String ticketList(Model model) {

    List<TicketModel> tickets =
            ticketService.getAllTickets();

    model.addAttribute(
            "tickets",
            tickets
    );

    model.addAttribute(
            "openCount",
            tickets.stream()
                    .filter(t ->
                            "OPEN".equals(t.getStatus()))
                    .count()
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

    return "customer/ticket-list";
}

/*
 * CREATE TICKET
 */
@GetMapping("/tickets/create")
public String createTicketForm(Model model) {

    model.addAttribute(
            "ticket",
            new TicketModel()
    );

    return "customer/ticket-create";
}

@PostMapping("/tickets/create")
public String createTicket(
        @ModelAttribute TicketModel ticket,
        Model model
) {

    boolean titleMissing =
            ticket.getTitle() == null
                    || ticket.getTitle().trim().isEmpty();

    boolean descriptionMissing =
            ticket.getDescription() == null
                    || ticket.getDescription().trim().isEmpty();

    if (titleMissing || descriptionMissing) {

        model.addAttribute(
                "ticket",
                ticket
        );

        model.addAttribute(
                "titleError",
                titleMissing
                        ? "Ticket title is required"
                        : null
        );

        model.addAttribute(
                "descriptionError",
                descriptionMissing
                        ? "Description is required"
                        : null
        );

        return "customer/ticket-create";
    }

    ticket.setCustomerId(CUSTOMER_ID);
    ticket.setTechnicianId(1);

    ticketService.createTicket(ticket);

    return "redirect:/customer/tickets";
}

/*
 * TICKET DETAIL
 */
@GetMapping("/tickets/{id:[0-9]+}")
public String ticketDetail(
        @PathVariable int id,
        Model model
) {

    TicketModel ticket =
            ticketService.getTicketById(id);

    model.addAttribute(
            "ticket",
            ticket
    );

    model.addAttribute(
            "resolutionNote",
            "Waiting for technician update"
    );

    return "customer/ticket-detail";
}

/*
 * PROFILE
 */
@GetMapping("/profile")
public String profile(Model model) {

    Subscription subscription =
            subscriptionService
                    .getActiveSubscriptionByCustomer(CUSTOMER_ID);

    model.addAttribute(
            "subscription",
            subscription
    );

    return "customer/profile";
}

}
