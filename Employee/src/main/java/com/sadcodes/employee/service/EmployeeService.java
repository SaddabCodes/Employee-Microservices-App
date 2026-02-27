package com.sadcodes.employee.service;

import com.sadcodes.employee.model.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto savedEmployeeDto(EmployeeDto employeeDto);

    EmployeeDto updateEmployeeDto(Long id, EmployeeDto employeeDto);

    void employeeDto(Long id);

    EmployeeDto getSingleEmployee(Long id);

    List<EmployeeDto>getAllEmployees();

}
