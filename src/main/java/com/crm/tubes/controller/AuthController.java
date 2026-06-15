package com.crm.tubes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping({"/", "/login"})
    public String loginPage(Model model, HttpSession session) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

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
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterRequest request,
            Model model,
            RedirectAttributes ra) {

        try {
            authService.register(request);

            ra.addFlashAttribute(
                    "success",
                    "Registrasi berhasil! Silakan login."
            );

            return "redirect:/login";

        } catch (RuntimeException e) {

            model.addAttribute("registerRequest", request);
            model.addAttribute("error", e.getMessage());

            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {

        model.addAttribute(
                "resetRequest",
                new ResetPasswordRequest()
        );

        return "forgetpass";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @ModelAttribute ResetPasswordRequest request,
            Model model,
            RedirectAttributes ra) {

        try {

            authService.validateEmailExists(
                    request.getEmail()
            );

            ra.addFlashAttribute(
                    "email",
                    request.getEmail()
            );

            return "redirect:/change-password";

        } catch (RuntimeException e) {

            model.addAttribute("resetRequest", request);
            model.addAttribute("error", e.getMessage());

            return "forgetpass";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {

        model.addAttribute(
                "resetRequest",
                new ResetPasswordRequest()
        );

        return "changepass";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute ResetPasswordRequest request,
            Model model,
            RedirectAttributes ra) {

        try {

            authService.resetPassword(request);

            ra.addFlashAttribute(
                    "success",
                    "Password berhasil diubah!"
            );

            return "redirect:/login";

        } catch (RuntimeException e) {

            model.addAttribute("resetRequest", request);
            model.addAttribute("error", e.getMessage());

            return "changepass";
        }
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {

        authService.logout(session);

        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        authService.logout(session);

        return "redirect:/login";
    }
}