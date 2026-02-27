package com.sadcodes.employee.service.impl;

import com.sadcodes.employee.model.dto.EmployeeDto;
import com.sadcodes.employee.repository.EmployeeRepository;
import com.sadcodes.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto savedEmployeeDto(EmployeeDto employeeDto) {
        return null;
    }

    @Override
    public EmployeeDto updateEmployeeDto(Long id, EmployeeDto employeeDto) {
        return null;
    }

    @Override
    public void employeeDto(Long id) {

    }

    @Override
    public EmployeeDto getSingleEmployee(Long id) {
        return null;
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return List.of();
    }
}
