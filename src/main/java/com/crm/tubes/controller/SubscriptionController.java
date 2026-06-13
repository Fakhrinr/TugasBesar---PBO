package com.crm.tubes.controller;

import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.User;
import com.crm.tubes.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * SubscriptionController = the class that handles HTTP requests.
 *
 * @Controller tells Spring Boot: "this class handles browser requests"
 * @RequestMapping("/subscription") means all URLs here start with /subscription
 *
 * Flow when user opens browser:
 *   1. Browser sends GET /subscription
 *   2. Controller receives it
 *   3. Controller calls Service to get data
 *   4. Controller puts data into Model (like a bag to carry to HTML)
 *   5. Controller returns the HTML page name
 *   6. Thymeleaf fills the HTML with the data from Model
 *   7. Browser shows the final page
 */
@Controller
@RequestMapping("/subscription")
public class SubscriptionController {

    /**
     * Spring provides the Service object automatically.
     * Controller only talks to Service — never to Repository directly.
     */
    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * showSubscriptionPage() — handles GET /subscription
     *
     * This is the MAIN page. It shows:
     * - Admin/Staff: active plan card + full subscriber table
     * - Customer: their own active plan + history
     *
     * @param model   the "bag" we use to send data to the HTML page
     * @param session the current user's session (to know who is logged in)
     */
    @GetMapping
    public String showSubscriptionPage(Model model, HttpSession session) {

        // ── Step 1: Get the logged-in user from session ───────────────────────
        // Your auth feature (Tiara's part) should store the User object in session
        // when the user logs in: session.setAttribute("loggedUser", user)
        User loggedUser = (User) session.getAttribute("loggedUser");

        // Safety check — if no one is logged in, redirect to login page
        if (loggedUser == null) {
            return "redirect:/login";
        }

        // ── Step 2: Send logged user to HTML (for avatar initials in topbar) ──
        model.addAttribute("loggedUser", loggedUser);

        // ── Step 3: Get the user's role as a String ───────────────────────────
        // Role is an enum in User class, we convert to String for Thymeleaf
        // e.g. Role.ADMIN → "ADMIN"
        String role = loggedUser.getRole().name();
        model.addAttribute("role", role);

        // ── Step 4: Load data based on role ───────────────────────────────────

        if (role.equals("ADMIN") || role.equals("CUSTOMER_SERVICE")) {

            // Admin/Staff sees ALL subscribers
            List<Subscription> subscribers = subscriptionService.getAllSubscriptions();
            model.addAttribute("subscribers", subscribers);

            // Show the first ACTIVE subscription as the "active plan" card
            // (or you can change this to a specific featured plan later)
            if (!subscribers.isEmpty()) {
                // Find the first ACTIVE one for the top card
                Subscription activePlan = subscribers.stream()
                        .filter(s -> s.getStatus().name().equals("ACTIVE"))
                        .findFirst()
                        .orElse(subscribers.get(0)); // fallback: show first if none active
                model.addAttribute("activePlan", activePlan);
            } else {
                // No subscriptions at all — send null so HTML shows empty state
                model.addAttribute("activePlan", null);
            }

        } else if (role.equals("CUSTOMER")) {

            // Customer only sees THEIR OWN subscription
            // loggedUser.getCustomerId() = the customer ID linked to this user account
            int customerId = loggedUser.getCustomerId();

            try {
                // Get their active/grace subscription
                Subscription myPlan = subscriptionService.getActiveSubscriptionByCustomer(customerId);
                model.addAttribute("myPlan", myPlan);

                // Get their full history (all subscriptions, including old ones)
                List<Subscription> myHistory = subscriptionService.getAllSubscriptions()
                        .stream()
                        .filter(s -> s.getCustomer().getId() == customerId)
                        .toList();
                model.addAttribute("myHistory", myHistory);

            } catch (Exception e) {
                // No subscription found for this customer
                model.addAttribute("myPlan", null);
                model.addAttribute("myHistory", List.of());
            }
        }

        // ── Step 5: Return the HTML template name ─────────────────────────────
        // Spring Boot looks for this file in: src/main/resources/frontend/
        // So this returns "frontend/subscription" → subscription.html
        return "frontend/subscription";
    }
}