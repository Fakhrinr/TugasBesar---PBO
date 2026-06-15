/* package com.crm.tubes.service;

import com.crm.tubes.model.TicketModel;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.model.UserModel.Role;
import com.crm.tubes.repository.TicketRepository;
import com.crm.tubes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

   public AdminDashboardData getAdminData() {

        return new AdminDashboardData(
                ticketRepository.countByStatus("OPEN"),
                ticketRepository.countByStatus("IN_PROGRESS"),
                ticketRepository.countByStatus("RESOLVED"),
                ticketRepository.countByStatus("CLOSED"),
                userRepository.countByRole(Role.CUSTOMER),
                userRepository.countByRole(Role.TEKNISI),
                ticketRepository.findTop5Recent(),
                ticketRepository.findByTeknisiIsNullOrderByCreatedAtDesc(),
                userRepository.findByRole(Role.TEKNISI)
        );
    }

    public TeknisiDashboardData getTeknisiData(UserModel teknisi) {

        return new TeknisiDashboardData(
                ticketRepository.countByTechnicianAndStatus(
                        teknisi.getId(),
                        "OPEN"
                ),
                ticketRepository.countByTechnicianAndStatus(
                        teknisi.getId(),
                        "IN_PROGRESS"
                ),
                ticketRepository.countByTechnicianAndStatus(
                        teknisi.getId(),
                        "RESOLVED"
                ),
                ticketRepository.findByTechnicianOrderByCreatedAtDesc(
                        teknisi.getId()
                )
        );
    }

    public CustomerDashboardData getCustomerData(UserModel customer) {

        return new CustomerDashboardData(
                ticketRepository.countByCustomerAndStatus(
                        customer.getCustomerId(),
                        "OPEN"
                ),
                ticketRepository.countByCustomerAndStatus(
                        customer.getCustomerId(),
                        "IN_PROGRESS"
                ),
                ticketRepository.countByCustomerAndStatus(
                        customer.getCustomerId(),
                        "RESOLVED"
                ),
                ticketRepository.findByCustomerOrderByCreatedAtDesc(
                        customer.getCustomerId()
                )
        );
    }

    public record AdminDashboardData(
            long openCount,
            long inProgressCount,
            long resolvedCount,
            long closedCount,
            long customerCount,
            long teknisiCount,
            List<TicketModel> recentTickets,
            List<TicketModel> unassignedTickets,
            List<UserModel> teknisiList
    ) {}

    public record TeknisiDashboardData(
            long openCount,
            long inProgressCount,
            long resolvedCount,
            List<TicketModel> myTickets
    ) {}

    public record CustomerDashboardData(
            long openCount,
            long inProgressCount,
            long resolvedCount,
            List<TicketModel> myTickets
    ) {}
}*/