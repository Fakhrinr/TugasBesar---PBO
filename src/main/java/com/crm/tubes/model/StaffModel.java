package com.crm.tubes.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StaffModel extends UserModel {

    private String employeeId;

    public void manageCustomer() {}

    public void handleTicket() {}

    public void manageBilling() {}
}