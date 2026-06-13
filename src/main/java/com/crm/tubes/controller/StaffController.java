package com.crm.tubes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.tubes.service.StaffService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/admin")
    public ResponseEntity<Integer> createAdmin(
            @RequestBody StaffService.CreateAdminRequest request) {

        return ResponseEntity.ok(
                staffService.createAdmin(request)
        );
    }

    @PostMapping("/technician")
    public ResponseEntity<Integer> createTechnician(
            @RequestBody StaffService.CreateTechnicianRequest request) {

        return ResponseEntity.ok(
                staffService.createTechnician(request)
        );
    }
}