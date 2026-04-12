package com.sadcodes.employee.service.impl;

import com.sadcodes.employee.client.AddressClient;
import com.sadcodes.employee.exception.BadRequestException;
import com.sadcodes.employee.exception.ResourceNotFoundException;
import com.sadcodes.employee.model.dto.AddressDto;
import com.sadcodes.employee.model.dto.EmployeeDto;
import com.sadcodes.employee.model.entity.Employee;
import com.sadcodes.employee.repository.EmployeeRepository;
import com.sadcodes.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final AddressClient addressClient;

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
    public EmployeeDto getSingleEmployee(Long id) throws InterruptedException {
        Thread.sleep(6000);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with id: " + id));
        List<AddressDto> addresses = new ArrayList<>();
        EmployeeDto dto = modelMapper.map(employee, EmployeeDto.class);
        try {
            addresses = addressClient.getAddressByEmpId(employee.getId());
            dto.setAddressDto(addresses);
        } catch (Exception e) {
            log.error("Address not found with employee id: {}", employee.getId());
        }
        return dto;
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found");
        }

        List<EmployeeDto> employeeDtoList = employees.stream().map(emp -> modelMapper.map(emp, EmployeeDto.class)).toList();
        List<EmployeeDto> response = new ArrayList<>();

        for (EmployeeDto employee : employeeDtoList) {
            List<AddressDto> addresses = new ArrayList<>();
            try {
                addresses = addressClient.getAddressByEmpId(employee.getId());
                employee.setAddressDto(addresses);
            } catch (Exception e) {
                employee.setAddressDto(new ArrayList<>());
                log.error("Address not found with employee id: {}", employee.getId());
            }
            response.add(employee);
        }
        return response;
    }


    @Override
    public EmployeeDto getEmployeeByEmpCodeAndCompanyName(String empCode, String companyName) {
        Employee employee = employeeRepository.findByEmpCodeAndCompanyName(empCode, companyName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with empCode " + empCode + " and companyName " + companyName));
        return modelMapper.map(employee, EmployeeDto.class);
    }
}
