package com.crm.tubes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserModel {

    private Integer id;
    private String  name;
    private String  email;
    private String  password;
    private Role    role;
    private Boolean status;

    private Integer customerId;

    public enum Role {
        ADMIN, TEKNISI, CUSTOMER
    }
}
