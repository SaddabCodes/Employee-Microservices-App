package com.sadcodes.address.model.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    Long id;
    String empName;
    String empEmail;
    String empCode;
    String companyName;
}