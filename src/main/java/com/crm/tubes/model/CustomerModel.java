package com.crm.tubes.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerModel extends UserModel {

    private String address;

    private String phone;

    public void viewInvoice() {}

    public void createTicket() {}

    public void viewSubscription() {}
}