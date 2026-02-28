package com.sadcodes.employee.service.impl;

import com.sadcodes.employee.model.dto.EmployeeDto;
import com.sadcodes.employee.model.entity.Employee;
import com.sadcodes.employee.repository.EmployeeRepository;
import com.sadcodes.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDto savedEmployeeDto(EmployeeDto employeeDto) {
        if (employeeDto.getId() != null) {
            throw new RuntimeException("Employee already exist");
        }
        Employee entity = modelMapper.map(employeeDto, Employee.class);
        Employee saveEntity = employeeRepository.save(entity);
        return modelMapper.map(saveEntity, EmployeeDto.class);
    }

    @Override
    public EmployeeDto updateEmployeeDto(Long id, EmployeeDto employeeDto) {
        if (id == null || employeeDto.getId() == null) {
            throw new RuntimeException("Please provide employee id");
        }
        employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        Employee entity = modelMapper.map(employeeDto, Employee.class);
        Employee updateEmployee = employeeRepository.save(entity);
        return modelMapper.map(updateEmployee, EmployeeDto.class);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        employeeRepository.delete(employee);

    }

    @Override
    public EmployeeDto getSingleEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employee = employeeRepository.findAll();
        return employee.stream().map(emp -> modelMapper.map(emp, EmployeeDto.class))
                .toList();
    }
}
