package com.crm.tubes.service;

import org.springframework.stereotype.Service;

import com.crm.tubes.model.AdminModel;
import com.crm.tubes.model.TechnicianModel;
import com.crm.tubes.model.UserModel;
import com.crm.tubes.repository.UserRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final UserRepository userRepository;

    public Integer createAdmin(
            CreateAdminRequest request
    ) {

        AdminModel admin =
                new AdminModel();

        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(request.getPassword());

        admin.setRole(
                UserModel.Role.ADMIN
        );

        admin.setStatus(true);

        admin.setEmployeeId(
                generateEmployeeId()
        );

        return userRepository.registerUser(
                admin
        );
    }

    public Integer createTechnician(
            CreateTechnicianRequest request
    ) {

        TechnicianModel technician =
                new TechnicianModel();

        technician.setName(
                request.getName()
        );

        technician.setEmail(
                request.getEmail()
        );

        technician.setPassword(
                request.getPassword()
        );

        technician.setArea(
                request.getArea()
        );

        technician.setRole(
                UserModel.Role.TEKNISI
        );

        technician.setStatus(true);

        technician.setEmployeeId(
                generateEmployeeId()
        );

        return userRepository.registerUser(
                technician
        );
    }

    private String generateEmployeeId() {

        return "EMP-" +
                System.currentTimeMillis();
    }

    @Data
    public static class CreateAdminRequest {

        private String name;
        private String email;
        private String password;
    }

    @Data
    public static class CreateTechnicianRequest {

        private String name;
        private String email;
        private String password;
        private String area;
    }
}