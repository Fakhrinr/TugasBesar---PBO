# Tugas Besar PBO - CRM ISP

## Manual Instalasi Sistem

Dokumen ini berisi panduan instalasi dan menjalankan aplikasi **CRM ISP** berbasis Spring Boot pada lingkungan pengembangan lokal.

Aplikasi ini merupakan sistem Customer Relationship Management (CRM) untuk mengelola layanan pelanggan ISP yang mencakup:
- Manajemen pelanggan
- Manajemen tiket gangguan
- Manajemen subscription/langganan
- Manajemen invoice
- Manajemen pembayaran
- Manajemen notifikasi


# 1. Spesifikasi Sistem

## 1.1 Teknologi yang Digunakan

| Komponen | Spesifikasi |
|---|---|
| Bahasa Pemrograman | Java 25 |
| Framework | Spring Boot 3.5.14 |
| Arsitektur | MVC (Model View Controller) |
| Build Tool | Maven (Maven Wrapper tersedia) |
| Database | MySQL |
| Driver Database | mysql-connector-j |
| Template Engine | Thymeleaf |
| Library Tambahan | Lombok |
| Frontend | HTML, CSS, JavaScript |


## 1.2 Prasyarat Sistem

Sebelum menjalankan aplikasi, pastikan:

- JDK 21 atau lebih baru sudah terinstall dan dikonfigurasi (`JAVA_HOME`)
- MySQL Server 8.x sudah berjalan
- (Opsional) IDE seperti IntelliJ IDEA / VS Code / Eclipse
- Browser modern (Chrome / Edge / Firefox)


# 2. Struktur Project

```
TugasBesar---PBO/
│
├── src/
│   └── main/
│       ├── java/com/crm/tubes/
│       │   ├── controller/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       │
│       └── resources/
│           ├── frontend/
│           ├── static/
│           ├── schema.sql
│           └── application.properties
│
├── pom.xml
├── mvnw
└── mvnw.cmd
```


# 3. Database Setup

Buat database MySQL:

```sql
CREATE DATABASE db_crm;
```

> Tabel akan otomatis dibuat oleh Spring Boot melalui `schema.sql` saat aplikasi pertama dijalankan.


# 4. Konfigurasi Application Properties

Edit file:

```
src/main/resources/application.properties
```

Isi konfigurasi:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_crm?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=<password_mysql_anda>

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql

spring.thymeleaf.prefix=classpath:/frontend/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false
```

Catatan:
- Ganti password sesuai MySQL masing-masing
- Jika port MySQL bukan 3306, sesuaikan URL


# 5. Install Dependency

Masuk ke folder project:

```bash
cd TugasBesar---PBO
```

Jalankan:

## Windows
```bash
mvnw.cmd clean install
```

## Linux / macOS
```bash
./mvnw clean install
```

Dependency yang akan di-download:
- Spring Boot Web
- Spring JDBC
- Thymeleaf
- MySQL Connector
- Lombok


# 6. Menjalankan Aplikasi

## Cara 1 - Maven Wrapper

### Windows
```bash
mvnw.cmd spring-boot:run
```

### Linux / macOS
```bash
./mvnw spring-boot:run
```


## Cara 2 - IDE

1. Import project sebagai Maven Project
2. Jalankan file:
```
TubesApplication.java
```
3. Pastikan muncul log:
```
Started TubesApplication
```


# 7. Akses Aplikasi

Buka browser:

```
http://localhost:8080/login
```

Role pengguna:
- Admin → full access sistem
- Staff / Customer Service → kelola pelanggan & tiket
- Teknisi → handle ticket gangguan
- Customer → akses layanan pribadi


# 8. Verifikasi

| Komponen | Status Berhasil |
|---|---|
| Database | Tidak error koneksi |
| Tabel | Otomatis terbentuk |
| Server | Started TubesApplication |
| Web | Login page muncul |
| Login | Bisa sesuai role |


# 9. Troubleshooting

| Masalah | Solusi |
|---|---|
| DB gagal konek | Cek MySQL hidup + password benar |
| Port 8080 dipakai | Ubah `server.port=8081` |
| Maven error | Cek internet + JDK 21+ |
| Tabel tidak muncul | cek `schema.sql` + `spring.sql.init.mode` |
| Frontend blank | cek folder `frontend` + thymeleaf config |


# 10. Info Tambahan

Stack:
- Spring Boot MVC
- JDBC
- Thymeleaf
- MySQL
- Maven Wrapper

Project ini bisa langsung dijalankan setelah mengikuti langkah di atas.
