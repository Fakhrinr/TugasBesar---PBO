package com.crm.tubes.model;

/**
 * Customer = model untuk data pelanggan ISP.
 * Aggregation dengan Subscription:
 * Subscription <>── Customer
 * (Customer bisa exist tanpa Subscription)
 */
public class Customer {

    private int id;
    private int userId;       // FK ke tabel user
    private String name;      // nama customer (dari JOIN ke tabel user)
    private String phone;     // nomor telepon
    private String address;   // alamat rumah

    // ── Constructor ───────────────────────────────────────
    public Customer() {}

    public Customer(int id, int userId, String name, String phone, String address) {
        this.id      = id;
        this.userId  = userId;
        this.name    = name;
        this.phone   = phone;
        this.address = address;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
