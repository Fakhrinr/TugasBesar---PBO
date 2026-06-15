/* package com.crm.tubes.controller;

import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.AuthService;
import com.crm.tubes.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AuthService authService;
    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(HttpSession session, Model model) {

        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        UserModel user = authService.getLoggedUser(session);
        model.addAttribute("user", user);

        return switch (user.getRole()) {
            case ADMIN    -> adminDashboard(model);
            case TEKNISI  -> teknisiDashboard(user, model);
            case CUSTOMER -> customerDashboard(user, model);
        };
    }

    // ────────────────────────────────────────────

    private String adminDashboard(Model model) {
        model.addAttribute("data", dashboardService.getAdminData());
        return "dashboard/admin";
    }

    private String teknisiDashboard(UserModel user, Model model) {
        model.addAttribute("data", dashboardService.getTeknisiData(user));
        return "dashboard/teknisi";
    }

    private String customerDashboard(UserModel user, Model model) {
        model.addAttribute("data", dashboardService.getCustomerData(user));
        return "dashboard/customer";
    }
}
*/