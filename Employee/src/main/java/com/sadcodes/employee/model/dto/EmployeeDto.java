package com.sadcodes.employee.model.dto;

import lombok.Data;


import java.io.Serializable;

@Data
public class EmployeeDto {
    Long id;
    String empName;
    String empEmail;
    String empCode;
    String companyName;
}