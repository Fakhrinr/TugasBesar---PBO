package com.crm.tubes.controller;

import com.crm.tubes.model.Subscription;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.SubscriptionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * SubscriptionController — handle request HTTP untuk halaman Subscription.
 *
 * @Controller     → Spring MVC, return nama HTML (bukan JSON)
 * @RequestMapping → semua URL di controller ini diawali /subscription
 * @RequiredArgsConstructor → Lombok, otomatis inject dependency lewat constructor
 */
@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    // Lombok @RequiredArgsConstructor otomatis inject ini
    // Ga perlu @Autowired lagi
    private final SubscriptionService subscriptionService;

    /**
     * showSubscriptionPage() — handle GET /subscription
     *
     * Cek role user yang login:
     * - ADMIN / TEKNISI → lihat semua subscriber
     * - CUSTOMER        → lihat subscription sendiri
     *
     * @param model   tempat naruh data yang dikirim ke HTML
     * @param session session user yang lagi login (dari Tiara's AuthService)
     */
    @GetMapping
    public String showSubscriptionPage(Model model, HttpSession session) {

        // ── Step 1: Ambil user dari session ──────────────────────────────
        // Key "loggedUser" sesuai yang Tiara set di AuthService.java:
        // session.setAttribute("loggedUser", user)
        UserModel loggedUser = (UserModel) session.getAttribute("loggedUser");

        // Kalau belum login → redirect ke halaman login
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }


        // ── Step 2: Kirim data user ke HTML ──────────────────────────────
        // Untuk nampilin nama/avatar di topbar
        model.addAttribute("loggedUser", loggedUser);

        // ── Step 3: Ambil role dari session ──────────────────────────────
        // Key "loggedUserRole" sesuai AuthService Tiara
        // Contoh nilainya: UserModel.Role.ADMIN, UserModel.Role.CUSTOMER, dll
        UserModel.Role role = (UserModel.Role) session.getAttribute("loggedUserRole");
        model.addAttribute("role", role);

        // ── Step 4: Load data sesuai role ────────────────────────────────

        if (role == UserModel.Role.ADMIN || role == UserModel.Role.TEKNISI) {

            // Admin & Teknisi lihat SEMUA subscriber
            List<Subscription> subscribers = subscriptionService.getAllSubscriptions();
            model.addAttribute("subscribers", subscribers);

            // Cari subscription ACTIVE pertama untuk card atas
            Subscription activePlan = subscribers.stream()
                    .filter(s -> s.getStatus() == com.crm.tubes.model.SubscriptionStatus.ACTIVE)
                    .findFirst()
                    .orElse(subscribers.isEmpty() ? null : subscribers.get(0));

            model.addAttribute("activePlan", activePlan);

        } else if (role == UserModel.Role.CUSTOMER) {

            // Customer hanya lihat subscription MEREKA SENDIRI
            // customerId ada di session dari Tiara: session.getAttribute("loggedUserId")
            Integer customerId = (Integer) session.getAttribute("loggedUserId");

            try {
                // Ambil subscription aktif milik customer ini
                Subscription myPlan = subscriptionService
                        .getActiveSubscriptionByCustomer(customerId);
                model.addAttribute("myPlan", myPlan);

                // Ambil history semua subscription customer ini
                List<Subscription> myHistory = subscriptionService
                        .getSubscriptionsByCustomerId(customerId);
                model.addAttribute("myHistory", myHistory);

            } catch (Exception e) {
                // Kalau ga ada subscription → kirim null, HTML tampilkan empty state
                model.addAttribute("myPlan", null);
                model.addAttribute("myHistory", List.of());
            }
        }

        // ── Step 5: Return nama file HTML ────────────────────────────────
        // Spring Boot cari file di: src/main/resources/frontend/subscription.html
        return "subscription";
    }
}