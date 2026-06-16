package com.crm.tubes.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.InvoiceStatus;
import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.SubscriptionStatus;
import com.crm.tubes.model.TicketModel;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.repository.InvoiceRepository;
import com.crm.tubes.repository.SubscriptionRepository;
import com.crm.tubes.repository.TicketRepository;
import com.crm.tubes.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;

    // ── ADMIN ─────────────────────────────────────────────────────────────────

    public AdminDashboardData getAdminData() {
        // Stat counts
        long totalCustomer  = userRepository.countByRole(UserModel.Role.CUSTOMER);
        long totalTeknisi   = userRepository.countByRole(UserModel.Role.TEKNISI);

        List<Subscription> allSubs = subscriptionRepository.findAll();
        long totalSubscriptionAktif = allSubs.stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .count();

        List<Invoice> allInvoices = invoiceRepository.findAll();
        long totalInvoiceBelumBayar = allInvoices.stream()
                .filter(i -> i.getStatus() != InvoiceStatus.PAID)
                .count();

        List<TicketModel> allTickets = ticketRepository.findAll();
        long openTicketCount = allTickets.stream()
                .filter(t -> "OPEN".equals(t.getStatus()))
                .count();

        // Recent tickets (last 5)
        List<TicketModel> recentTickets = allTickets.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Recent invoices (last 5)
        List<Invoice> recentInvoices = allInvoices.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Expiring soon: subscriptions with status GRACE or near expiry
        List<Subscription> expiringSubscriptions = allSubs.stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.GRACE
                          || s.getStatus() == SubscriptionStatus.SUSPENDED)
                .limit(5)
                .collect(Collectors.toList());

        // Teknisi list for assign modal
        List<UserModel> teknisiList = userRepository.findByRole(UserModel.Role.TEKNISI);

        return new AdminDashboardData(
                totalCustomer,
                totalTeknisi,
                totalSubscriptionAktif,
                totalInvoiceBelumBayar,
                openTicketCount,
                recentTickets,
                recentInvoices,
                expiringSubscriptions,
                teknisiList
        );

    }

    // ── TEKNISI ───────────────────────────────────────────────────────────────

    public TeknisiDashboardData getTeknisiData(UserModel teknisi) {
        List<TicketModel> allTickets = ticketRepository.findAll();

        // Filter hanya ticket yang di-assign ke teknisi ini
        List<TicketModel> myTickets = allTickets.stream()
                .filter(t -> teknisi.getId() != null
                          && teknisi.getId().equals(t.getTechnicianId()))
                .collect(Collectors.toList());

        long assignedCount   = myTickets.stream().filter(t -> "OPEN".equals(t.getStatus())).count();
        long inProgressCount = myTickets.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        long resolvedCount   = myTickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count();
        long closedCount     = myTickets.stream().filter(t -> "CLOSED".equals(t.getStatus())).count();

        // Subscription untuk referensi customer (ambil semua, read-only)
        List<Subscription> subscriptions = subscriptionRepository.findAll().stream()
                .limit(10)
                .collect(Collectors.toList());

        return new TeknisiDashboardData(
                assignedCount,
                inProgressCount,
                resolvedCount,
                closedCount,
                myTickets,
                subscriptions
        );
    }

    // ── Data Records ──────────────────────────────────────────────────────────

    public record AdminDashboardData(
            long totalCustomer,
            long totalTeknisi,
            long totalSubscriptionAktif,
            long totalInvoiceBelumBayar,
            long openTicketCount,
            List<TicketModel> recentTickets,
            List<Invoice> recentInvoices,
            List<Subscription> expiringSubscriptions,
            List<UserModel> teknisiList
    ) {}

    public record TeknisiDashboardData(
            long assignedCount,
            long inProgressCount,
            long resolvedCount,
            long closedCount,
            List<TicketModel> myTickets,
            List<Subscription> subscriptions
    ) {}

    @Data
@AllArgsConstructor
public static class CustomerDashboardData {

    private Subscription subscription;
    private List<TicketModel> myTickets;
    private List<Invoice> myInvoices;

    private long unpaidInvoiceCount;
    private long myTicketCount;
}

    // ── CUSTOMER ───────────────────────────────────────────────────────────────

public CustomerDashboardData getCustomerData(UserModel customer) {

    Subscription subscription = subscriptionRepository.findAll()
            .stream()
            .filter(s -> s.getCustomer() != null)
            .filter(s -> s.getCustomer().getId().equals(customer.getId()))
            .findFirst()
            .orElse(null);

    List<TicketModel> myTickets = ticketRepository.findAll()
            .stream()
            .filter(t -> customer.getId().equals(t.getCustomerId()))
            .toList();

    List<Invoice> myInvoices = invoiceRepository.findAll()
            .stream()
            .filter(i -> i.getSubscription() != null)
            .filter(i -> i.getSubscription().getCustomer() != null)
            .filter(i -> i.getSubscription().getCustomer().getId().equals(customer.getId()))
            .toList();

    long unpaidInvoiceCount = myInvoices.stream()
            .filter(i -> i.getStatus() != InvoiceStatus.PAID)
            .count();

    long myTicketCount = myTickets.size();

    return new CustomerDashboardData(
            subscription,
            myTickets,
            myInvoices,
            unpaidInvoiceCount,
            myTicketCount
    );
}
}