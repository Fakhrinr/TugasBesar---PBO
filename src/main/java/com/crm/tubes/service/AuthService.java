package com.crm.tubes.service;

import org.springframework.stereotype.Service;

import com.crm.tubes.model.CustomerModel;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public RegisterResponse register(
            RegisterRequest request
    ) {

        UserModel existingUser =
                userRepository.findByEmail(
                        request.getEmail()
                );

        if (existingUser != null) {

            throw new RuntimeException(
                    "Email sudah digunakan"
            );
        }

        CustomerModel customer =
                new CustomerModel();

        customer.setName(
                request.getName()
        );

        customer.setEmail(
                request.getEmail()
        );

        customer.setPassword(
                request.getPassword()
        );

        customer.setAddress(
                request.getAddress()
        );

        customer.setPhone(
                request.getPhone()
        );

        customer.setRole(
                UserModel.Role.CUSTOMER
        );

        customer.setStatus(true);

        Integer userId =
                userRepository.registerUser(
                        customer
                );

        return new RegisterResponse(
                userId,
                "Registrasi berhasil"
        );
    }

    public LoginResponse login(
            LoginRequest request
    ) {

        UserModel user =
                userRepository.findByEmail(
                        request.getEmail()
                );

        if (user == null) {

            throw new RuntimeException(
                    "User tidak ditemukan"
            );
        }

        if (!user.getStatus()) {

            throw new RuntimeException(
                    "Akun tidak aktif"
            );
        }

        if (!user.getPassword().equals(
                request.getPassword()
        )) {

            throw new RuntimeException(
                    "Password salah"
            );
        }

        return new LoginResponse(
                user.getIdUser(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    public void logout(
            Integer userId
    ) {

        userRepository.updateStatus(
                userId,
                false
        );
    }

    public void resetPassword(
            ResetPasswordRequest request
    ) {

        UserModel user =
                userRepository.findByEmail(
                        request.getEmail()
                );

        if (user == null) {

            throw new RuntimeException(
                    "User tidak ditemukan"
            );
        }

        userRepository.updatePassword(
                request.getEmail(),
                request.getNewPassword()
        );
    }

    // ==================================================
    // REQUEST DTO
    // ==================================================

    @Data
    public static class RegisterRequest {

        private String name;

        private String email;

        private String password;

        private String address;

        private String phone;
    }

    @Data
    public static class LoginRequest {

        private String email;

        private String password;
    }

    @Data
    public static class ResetPasswordRequest {

        private String email;

        private String newPassword;
    }

    // ==================================================
    // RESPONSE DTO
    // ==================================================

    @Data
    @AllArgsConstructor
    public static class RegisterResponse {

        private Integer id;

        private String message;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {

        private Integer id;

        private String name;

        private String email;

        private UserModel.Role role;

        private Boolean status;
    }
}