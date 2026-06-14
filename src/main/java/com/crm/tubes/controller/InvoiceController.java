package com.crm.tubes.controller;

import com.crm.tubes.model.Invoice;
import com.crm.tubes.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(
            InvoiceService invoiceService
    ) {
        this.invoiceService = invoiceService;
    }

    /**
     * Display all invoices
     */
    @GetMapping
    public String getAllInvoices(
            Model model
    ) {

        model.addAttribute(
                "invoices",
                invoiceService.getAllInvoices()
        );

        return "invoice-list";
    }

    /**
     * Display invoice detail
     */
    @GetMapping("/{id}")
    public String getInvoiceDetail(
            @PathVariable int id,
            Model model
    ) {

        Invoice invoice =
                invoiceService.getInvoiceById(id);

        model.addAttribute(
                "invoice",
                invoice
        );

        model.addAttribute(
                "total",
                invoice.calculateTotal()
        );

        return "invoice-detail";
    }

    /**
     * Create invoice
     */
    @PostMapping
    public String createInvoice(
            @ModelAttribute Invoice invoice
    ) {

        invoiceService.createInvoice(
                invoice
        );

        return "redirect:/invoices";
    }

    /**
     * Mark invoice as paid
     */
    @PostMapping("/{id}/pay")
    public String markAsPaid(
            @PathVariable int id
    ) {

        invoiceService.markAsPaid(id);

        return "redirect:/invoices/" + id;
    }

    /**
     * Apply late fee
     */
    @PostMapping("/{id}/late-fee")
    public String applyLateFee(
            @PathVariable int id
    ) {

        invoiceService.applyLateFee(
                id,
                BigDecimal.valueOf(5.00)
        );

        return "redirect:/invoices/" + id;
    }

    /**
     * Refresh invoice status
     */
    @PostMapping("/{id}/check-status")
    public String checkStatus(
            @PathVariable int id
    ) {

        invoiceService.checkInvoiceStatus(id);

        return "redirect:/invoices/" + id;
    }
}