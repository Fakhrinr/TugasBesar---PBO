-- ============================================================
-- schema.sql
-- CRM ISP - Tugas Besar PBO
-- Kelompok 5 - IF 48 03
-- ============================================================
-- Taruh file ini di: src/main/resources/schema.sql
-- Spring Boot akan otomatis menjalankan file ini saat startup
-- (pastikan application.properties sudah dikonfigurasi)
-- ============================================================


-- Hapus tabel lama kalau ada (urutan penting! child dulu baru parent)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS invoice;        -- ← tambah invoice sebelum subscription
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS user;


-- ============================================================
-- TABEL USER
-- Dipakai oleh: Tiara (Authentication)
-- Semua orang yang bisa login: Admin, Customer Service,
--                              Teknisi, Finance, Customer
-- ============================================================
CREATE TABLE user (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,                    -- nama lengkap user
    email    VARCHAR(100) NOT NULL UNIQUE,             -- untuk login
    password VARCHAR(255) NOT NULL,                    -- disimpan terenkripsi
    role     ENUM(
                'ADMIN',
                'TEKNISI',
                'CUSTOMER'
             ) NOT NULL,
    status   BOOLEAN      NOT NULL DEFAULT TRUE        -- TRUE = aktif, FALSE = nonaktif
);


-- ============================================================
-- TABEL CUSTOMER
-- Dipakai oleh: Subscription (Abdan) + Billing (Shidqi)
-- Customer adalah user dengan role CUSTOMER
-- Aggregation: Customer <>── User (customer punya 1 user account)
-- ============================================================
CREATE TABLE customer (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL UNIQUE,           -- FK ke tabel user
    address    VARCHAR(255) NOT NULL,                  -- alamat rumah
    phone      VARCHAR(20)  NOT NULL,                  -- nomor telepon

    CONSTRAINT fk_customer_user
        FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE CASCADE                              -- hapus user → hapus customer
);


-- ============================================================
-- TABEL STAFF
-- Dipakai oleh: Authentication (Tiara) + Ticket (Nurendra)
-- Staff adalah user dengan role ADMIN / CS / TEKNISI / FINANCE
-- ============================================================
CREATE TABLE staff (
    id          INT         AUTO_INCREMENT PRIMARY KEY,
    user_id     INT         NOT NULL UNIQUE,           -- FK ke tabel user
    employee_id VARCHAR(20) NOT NULL UNIQUE,           -- NIP / kode karyawan

    CONSTRAINT fk_staff_user
        FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS technician (
    id      INT          AUTO_INCREMENT PRIMARY KEY,
    user_id INT          NOT NULL UNIQUE,
    area    VARCHAR(100) NOT NULL,
    CONSTRAINT fk_technician_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- ============================================================
-- TABEL SUBSCRIPTION
-- Dipakai oleh: Abdan (fitur ini!)
-- Menyimpan data langganan internet setiap customer
-- Aggregation: Subscription <>── Customer
-- ============================================================
CREATE TABLE subscription (
    id           INT            AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT            NOT NULL,              -- FK ke tabel customer
    plan_name    VARCHAR(100)   NOT NULL,              -- "Starter Plan", "Pro Plan", dll
    start_date   DATE           NOT NULL,              -- tanggal mulai berlangganan
    end_date     DATE           NOT NULL,              -- tanggal berakhir berlangganan
    monthly_fee  DECIMAL(10,2)  NOT NULL,              -- biaya per bulan, e.g. 29.99
    status       ENUM(
                    'ACTIVE',
                    'GRACE',
                    'SUSPENDED'
                 ) NOT NULL DEFAULT 'ACTIVE',          -- status otomatis dari checkStatus()

    CONSTRAINT fk_subscription_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id)
        ON DELETE CASCADE                              -- hapus customer → hapus subscription
);


-- ============================================================
-- TABEL INVOICE (Billing)
-- Dipakai oleh: Shidqi (Billing & Invoice)
-- Setiap subscription punya invoice bulanan
-- ============================================================
CREATE TABLE invoice (
    id              INT            AUTO_INCREMENT PRIMARY KEY,
    subscription_id INT            NOT NULL,           -- FK ke tabel subscription
    issue_date      DATE           NOT NULL,           -- tanggal invoice dibuat
    due_date        DATE           NOT NULL,           -- tanggal jatuh tempo bayar
    total_amount    DECIMAL(10,2)  NOT NULL,           -- total tagihan sebelum denda
    late_fee_amount DECIMAL(10,2)  NOT NULL DEFAULT 0, -- denda keterlambatan
    status          ENUM(
                        'DRAFT',
                        'OPEN',
                        'OVERDUE',
                        'PAID'
                    ) NOT NULL DEFAULT 'DRAFT',

    CONSTRAINT fk_invoice_subscription
        FOREIGN KEY (subscription_id) REFERENCES subscription(id)
        ON DELETE CASCADE
);


-- ============================================================
-- TABEL PAYMENT
-- Dipakai oleh: Shidqi (Billing & Invoice)
-- Menyimpan data pembayaran dari customer
-- ============================================================
CREATE TABLE payment (
    id           INT            AUTO_INCREMENT PRIMARY KEY,
    invoice_id   INT            NOT NULL,              -- FK ke tabel invoice
    payment_date DATE           NOT NULL,              -- tanggal bayar
    amount       DECIMAL(10,2)  NOT NULL,              -- jumlah yang dibayar
    status       ENUM(
                    'PENDING',
                    'SUCCESS',
                    'FAILED'
                 ) NOT NULL DEFAULT 'PENDING',

    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoice(id)
        ON DELETE CASCADE
);


-- ============================================================
-- TABEL NOTIFICATION
-- Dipakai oleh: Fakhri (Notification & Reminder)
-- Menyimpan notifikasi untuk setiap user
-- ============================================================
CREATE TABLE notification (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL,                  -- FK ke tabel user (siapa yang dinotif)
    type       VARCHAR(50)  NOT NULL,                  -- e.g. "PAYMENT_DUE", "INVOICE_CREATED"
    message    VARCHAR(255) NOT NULL,                  -- isi pesan notifikasi
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,    -- FALSE = belum dibaca
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE CASCADE
);


-- ============================================================
-- TABEL TICKET
-- Dipakai oleh: Nurendra (Trouble Ticketing)
-- Menyimpan laporan gangguan dari customer
-- ============================================================
CREATE TABLE ticket (
    id           INT          AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT          NOT NULL,                -- FK ke customer yang lapor
    technician_id INT         NULL,                    -- FK ke staff (teknisi yang ditugaskan)
    title        VARCHAR(100) NOT NULL,                -- judul masalah
    description  TEXT         NOT NULL,                -- detail masalah
    priority     ENUM(
                    'LOW',
                    'MEDIUM',
                    'HIGH',
					'URGENT'
                 ) NOT NULL DEFAULT 'MEDIUM',
    status       ENUM(
                    'OPEN',
                    'IN_PROGRESS',
                    'RESOLVED',
                    'CLOSED'
                 ) NOT NULL DEFAULT 'OPEN',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ticket_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ticket_technician
        FOREIGN KEY (technician_id) REFERENCES staff(id)
        ON DELETE SET NULL                             -- teknisi dihapus → ticket tetap ada
);


-- ============================================================
-- SAMPLE DATA (untuk testing)
-- Bisa dihapus kalau sudah production
-- ============================================================

-- Sample users
INSERT INTO user (name, email, password, role, status) VALUES
('Admin CRM',       'admin@crm.com',    'admin123',   'ADMIN',            TRUE),
('Larry Luciano',   'larry@gmail.com',  'larry123',   'CUSTOMER',         TRUE),
('Diana Reyes',     'diana@gmail.com',  'diana123',   'CUSTOMER',         TRUE),
('Andi Teknisi',    'andi@crm.com',     'andi123',    'TEKNISI',          TRUE);

-- Sample customers (linked to user)
INSERT INTO customer (user_id, address, phone) VALUES
(2, 'Jl. Merdeka No. 1, Bandung',   '081234567890'),  -- Larry
(3, 'Jl. Sudirman No. 5, Jakarta',  '081298765432');  -- Diana

-- Sample staff
INSERT INTO staff (user_id, employee_id) VALUES
(1, 'EMP-001'),   -- Admin
(4, 'EMP-002');   -- Budi CS

-- Sample subscriptions
INSERT INTO subscription (customer_id, plan_name, start_date, end_date, monthly_fee, status) VALUES
(1, 'Starter Plan',  '2026-04-01', '2026-05-01', 29.99, 'ACTIVE'),
(2, 'Pro Plan',      '2026-03-15', '2026-04-15', 59.99, 'GRACE');
