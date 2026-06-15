package com.crm.tubes.service;
 
import org.springframework.stereotype.Service;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class AuthService {
 
    private final UserRepository userRepository;
 
    // Satu-satunya key session — simpan objek UserModel lengkap
    private static final String SESSION_KEY = "loggedUser";
 
    // ── LOGIN ──────────────────────────────────────────────────────────
    public UserModel login(LoginRequest req, HttpSession session) {
        validate(req.getEmail(), "Email wajib diisi");
        validate(req.getPassword(), "Password wajib diisi");
 
        UserModel user = userRepository.findByEmail(req.getEmail());
        if (user == null)                          throw new RuntimeException("Email tidak ditemukan");
        if (!user.getPassword().equals(req.getPassword())) throw new RuntimeException("Password salah");
        if (!Boolean.TRUE.equals(user.getStatus())) throw new RuntimeException("Akun tidak aktif");
 
        // Kalau CUSTOMER → ambil customer.id dan simpan di user.customerId
        if (user.getRole() == UserModel.Role.CUSTOMER) {
            Integer customerId = userRepository.findCustomerIdByUserId(user.getId());
            user.setCustomerId(customerId); // customer.id tersimpan di sini
        }
 
        session.setAttribute(SESSION_KEY, user);
        return user;
    }
 
    // ── REGISTER (CUSTOMER only) ───────────────────────────────────────
    public Integer register(RegisterRequest req) {
        validate(req.getName(),    "Nama wajib diisi");
        validate(req.getEmail(),   "Email wajib diisi");
        validate(req.getPassword(),"Password wajib diisi");
        validate(req.getAddress(), "Alamat wajib diisi");
        validate(req.getPhone(),   "Nomor telepon wajib diisi");
 
        if (userRepository.findByEmail(req.getEmail()) != null)
            throw new RuntimeException("Email sudah digunakan");
 
        CustomerModel customer = new CustomerModel();
        customer.setName(req.getName());
        customer.setEmail(req.getEmail());
        customer.setPassword(req.getPassword());
        customer.setAddress(req.getAddress());
        customer.setPhone(req.getPhone());
        customer.setRole(UserModel.Role.CUSTOMER);
        customer.setStatus(true);
 
        return userRepository.registerUser(customer);
    }
 
    // ── FORGOT PASSWORD ────────────────────────────────────────────────
    public void validateEmailExists(String email) {
        validate(email, "Email wajib diisi");
        if (userRepository.findByEmail(email) == null)
            throw new RuntimeException("Email tidak ditemukan");
    }
 
    public void resetPassword(ResetPasswordRequest req) {
        validate(req.getEmail(),       "Email wajib diisi");
        validate(req.getNewPassword(), "Password baru wajib diisi");
 
        UserModel user = userRepository.findByEmail(req.getEmail());
        if (user == null) throw new RuntimeException("Email tidak ditemukan");
 
        userRepository.updatePassword(user.getId(), req.getNewPassword());
    }
 
    // ── LOGOUT ────────────────────────────────────────────────────────
    public void logout(HttpSession session) { session.invalidate(); }
 
    // ── SESSION HELPERS ───────────────────────────────────────────────
    public UserModel getLoggedUser(HttpSession session) {
        return (UserModel) session.getAttribute(SESSION_KEY);
    }
 
    public boolean isLoggedIn(HttpSession session)    { 
        return getLoggedUser(session) != null; 
    }
    public boolean isAdmin(HttpSession session)       { 
        return hasRole(session, UserModel.Role.ADMIN); 
    }
    public boolean isTechnician(HttpSession session)  { 
        return hasRole(session, UserModel.Role.TEKNISI); 
    }
    public boolean isCustomer(HttpSession session)    { 
        return hasRole(session, UserModel.Role.CUSTOMER); 
    }
 
    private boolean hasRole(HttpSession session, UserModel.Role role) {
        UserModel u = getLoggedUser(session);
        return u != null && u.getRole() == role;
    }
 
    private void validate(String value, String message) {
        if (value == null || value.isBlank()) throw new RuntimeException(message);
    }
 
    // ── DTO ───────────────────────────────────────────────────────────
    @Data public static class LoginRequest    { 
        private String email; private String password; 
    }
    @Data public static class RegisterRequest { 
        private String name; private String email; private String password; private String address; private String phone; 
    }
    @Data public static class ResetPasswordRequest { 
        private String email; private String newPassword; 
    }
}
 