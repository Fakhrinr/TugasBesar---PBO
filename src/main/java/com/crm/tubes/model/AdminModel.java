package com.crm.tubes.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminModel extends StaffModel {

    public void manageUser() {}

    public void configureSystem() {}
}