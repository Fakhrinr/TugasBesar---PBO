package com.crm.tubes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private Integer id;
    private String  name;
    private String  email;
    private String  password;
    private Role    role;
    private Boolean status;

    // populated after login for CUSTOMER role
    private Integer customerId;

    public enum Role {
        ADMIN, TEKNISI, CUSTOMER
    }
}
