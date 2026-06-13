package com.crm.tubes.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketModel {

    private int id;
    private int customerId;
    private Integer technicianId;

    private String title;
    private String description;

    private String priority;
    private String status;

    private Timestamp createdAt;
}