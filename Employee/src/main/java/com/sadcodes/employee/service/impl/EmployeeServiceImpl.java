package com.sadcodes.employee.service.impl;

import com.sadcodes.employee.exception.BadRequestException;
import com.sadcodes.employee.exception.ResourceNotFoundException;
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
            throw new ResourceNotFoundException("Employee already exist");
        }
        Employee entity = modelMapper.map(employeeDto, Employee.class);
        Employee saveEntity = employeeRepository.save(entity);
        return modelMapper.map(saveEntity, EmployeeDto.class);
    }

    @Override
    public EmployeeDto updateEmployeeDto(Long id, EmployeeDto employeeDto) {

        if (id == null) {
            throw new BadRequestException("Please provide employee id");
        }

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id: " + id)
                );

        // Update fields manually
        existingEmployee.setEmpName(employeeDto.getEmpName());
        existingEmployee.setEmpEmail(employeeDto.getEmpEmail());
        existingEmployee.setEmpCode(employeeDto.getEmpCode());
        existingEmployee.setCompanyName(employeeDto.getCompanyName());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        return modelMapper.map(updatedEmployee, EmployeeDto.class);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);

    }

    @Override
    public EmployeeDto getSingleEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id: " + id));
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employee = employeeRepository.findAll();
        if (employee.isEmpty()){
            throw new ResourceNotFoundException("Employee not found");
        }
        return employee.stream().map(emp -> modelMapper.map(emp, EmployeeDto.class))
                .toList();
    }
}
