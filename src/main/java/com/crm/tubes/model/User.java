package com.crm.tubes.model;

/**
 * User = model untuk semua user yang bisa login.
 * Basic version — Tiara akan expand ini untuk Auth feature.
 *
 * Role yang ada:
 * ADMIN, CUSTOMER_SERVICE, TEKNISI, FINANCE, CUSTOMER
 */
public class User {

    // ── Role enum (nested di dalam User) ──────────────────
    public enum Role {
        ADMIN,
        CUSTOMER_SERVICE,
        TEKNISI,
        FINANCE,
        CUSTOMER
    }

    private int id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean status;   // true = aktif, false = nonaktif

    // customerId dipakai kalau role = CUSTOMER
    // untuk nyari subscription milik dia
    private int customerId;

    // ── Constructor ───────────────────────────────────────
    public User() {}

    public User(int id, String name, String email,
                String password, Role role, boolean status) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.status   = status;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
}
