package com.crm.tubes.controller;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.service.InvoiceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // ── Helper: cek login ──────────────────────────────
    private UserModel getLoggedUser(HttpSession session) {
        return (UserModel) session.getAttribute("loggedUser");
    }

    // ── Helper: cek apakah admin ──────────────────────
    private boolean isAdmin(UserModel user) {
        return user.getRole() == UserModel.Role.ADMIN;
    }

    // ── Helper: cek apakah customer ───────────────────
    private boolean isCustomer(UserModel user) {
        return user.getRole() == UserModel.Role.CUSTOMER;
    }

    /**
     * Display all invoices
     * Admin    → lihat semua invoice
     * Customer → lihat invoice miliknya saja
     * Teknisi  → tidak punya akses
     */
    @GetMapping
    public String getAllInvoices(Model model, HttpSession session) {

        UserModel loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/login";

        if (isAdmin(loggedUser)) {
            // Admin lihat semua invoice
            model.addAttribute("invoices", invoiceService.getAllInvoices());

        } else if (isCustomer(loggedUser)) {
            // Customer hanya lihat invoice miliknya
            model.addAttribute("invoices",
                invoiceService.getInvoicesByCustomerId(loggedUser.getCustomerId()));

        } else {
            // Role lain (TEKNISI) tidak punya akses
            return "redirect:/unauthorized";
        }

        model.addAttribute("loggedUser", loggedUser);
        return "invoice-list";
    }

    /**
     * Display invoice detail
     * Admin    → bisa lihat semua invoice
     * Customer → hanya bisa lihat invoice miliknya
     * Teknisi  → tidak punya akses
     */
    @GetMapping("/{id}")
    public String getInvoiceDetail(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {

        UserModel loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/login";

        Invoice invoice = invoiceService.getInvoiceById(id);

        if (isCustomer(loggedUser)) {
            // Pastikan invoice ini milik customer yang login
            int ownerCustomerId = invoice.getSubscription().getCustomer().getCustomerId();
            if (ownerCustomerId != loggedUser.getCustomerId()) {
                return "redirect:/unauthorized";
            }
        } else if (!isAdmin(loggedUser)) {
            return "redirect:/unauthorized";
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("invoice", invoice);
        model.addAttribute("total", invoice.calculateTotal());

        return "invoice-detail";
    }

    /**
     * Create invoice
     * Hanya Admin
     */
    @PostMapping
    public String createInvoice(@ModelAttribute Invoice invoice,
                                HttpSession session) {

        UserModel loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/login";
        if (!isAdmin(loggedUser)) return "redirect:/unauthorized";

        invoiceService.createInvoice(invoice);
        return "redirect:/invoices";
    }

    /**
     * Mark invoice as paid
     * Hanya Admin
     */
    @PostMapping("/{id}/pay")
    public String markAsPaid(@PathVariable int id, HttpSession session) {

        UserModel loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/login";
        if (!isAdmin(loggedUser)) return "redirect:/unauthorized";

        invoiceService.markAsPaid(id);
        return "redirect:/invoices/" + id;
    }

    /**
     * Apply late fee
     * Hanya Admin
     */
    @PostMapping("/{id}/late-fee")
    public String applyLateFee(@PathVariable int id, HttpSession session) {

        UserModel loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/login";
        if (!isAdmin(loggedUser)) return "redirect:/unauthorized";

        invoiceService.applyLateFee(id, BigDecimal.valueOf(50000));
        return "redirect:/invoices/" + id;
    }

    /**
     * Refresh invoice status
     * Hanya Admin
     */
    @PostMapping("/{id}/check-status")
    public String checkStatus(@PathVariable int id, HttpSession session) {

        UserModel loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/login";
        if (!isAdmin(loggedUser)) return "redirect:/unauthorized";

        invoiceService.checkInvoiceStatus(id);
        return "redirect:/invoices/" + id;
    }
}