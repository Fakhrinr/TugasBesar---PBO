package com.crm.tubes.controller;

import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.AuthService;
import com.crm.tubes.service.SubscriptionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final AuthService authService;  // FIX: inject AuthService

    @GetMapping
    public String showSubscriptionPage(Model model, HttpSession session) {

        // FIX: gunakan AuthService, bukan baca session manual
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";  // FIX: endpoint login yang benar
        }

        UserModel loggedUser = authService.getLoggedUser(session);
        model.addAttribute("loggedUser", loggedUser);

        UserModel.Role role = loggedUser.getRole();
        model.addAttribute("role", role);

        if (role == UserModel.Role.ADMIN || role == UserModel.Role.TEKNISI) {

            List<Subscription> subscribers = subscriptionService.getAllSubscriptions();
            model.addAttribute("subscribers", subscribers);

            Subscription activePlan = subscribers.stream()
                    .filter(s -> s.getStatus() == com.crm.tubes.model.SubscriptionStatus.ACTIVE)
                    .findFirst()
                    .orElse(subscribers.isEmpty() ? null : subscribers.get(0));

            model.addAttribute("activePlan", activePlan);

        } else if (role == UserModel.Role.CUSTOMER) {

            // FIX: ambil customerId dari loggedUser (bukan dari session terpisah)
            Integer customerId = loggedUser.getCustomerId();

            try {
                Subscription myPlan = subscriptionService
                        .getActiveSubscriptionByCustomer(customerId);
                model.addAttribute("myPlan", myPlan);

                List<Subscription> myHistory = subscriptionService
                        .getSubscriptionsByCustomerId(customerId);
                model.addAttribute("myHistory", myHistory);

            } catch (Exception e) {
                model.addAttribute("myPlan", null);
                model.addAttribute("myHistory", List.of());
            }
        }

        return "subscription";
    }
}
