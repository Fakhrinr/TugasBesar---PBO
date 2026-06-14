package com.crm.tubes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.AuthService;
import com.crm.tubes.service.AuthService.LoginRequest;
import com.crm.tubes.service.AuthService.RegisterRequest;
import com.crm.tubes.service.AuthService.ResetPasswordRequest;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── GET ──────────────────────────────────────────────────

    @GetMapping({"/", "/login"})
    public String loginPage(Model model, HttpSession session) {
        if (authService.isLoggedIn(session)) return "redirect:/dashboard";
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (authService.isLoggedIn(session)) return "redirect:/dashboard";
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @GetMapping("/forgot-password")
    public String forgotPage(Model model) {
        model.addAttribute("resetRequest", new ResetPasswordRequest());
        return "auth/forgot-password";
    }

    @GetMapping("/change-password")
    public String changePage(Model model) {
        model.addAttribute("resetRequest", new ResetPasswordRequest());
        return "auth/change-password";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (!authService.isLoggedIn(session)) return "redirect:/login";

        UserModel user = authService.getLoggedUser(session);

        // Role-based redirect (expand later)
        return switch (user.getRole()) {
            case ADMIN    -> "dashboard/admin";
            case TEKNISI  -> "dashboard/teknisi";
            case CUSTOMER -> "dashboard/customer";
        };
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        authService.logout(session);
        return "redirect:/login";
    }

    // ── POST ─────────────────────────────────────────────────

    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequest request,
            HttpSession session,
            Model model) {
        try {
            authService.login(request, session);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("loginRequest", request);
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterRequest request,
            Model model,
            RedirectAttributes ra) {
        try {
            authService.register(request);
            ra.addFlashAttribute("success", "Registrasi berhasil! Silakan login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("registerRequest", request);
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @ModelAttribute ResetPasswordRequest request,
            Model model,
            RedirectAttributes ra) {
        try {
            authService.validateEmailExists(request.getEmail());
            ra.addFlashAttribute("email", request.getEmail());
            return "redirect:/change-password";
        } catch (RuntimeException e) {
            model.addAttribute("resetRequest", request);
            model.addAttribute("error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute ResetPasswordRequest request,
            Model model,
            RedirectAttributes ra) {
        try {
            authService.resetPassword(request);
            ra.addFlashAttribute("success", "Password berhasil diubah!");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("resetRequest", request);
            model.addAttribute("error", e.getMessage());
            return "auth/change-password";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/login";
    }

}
