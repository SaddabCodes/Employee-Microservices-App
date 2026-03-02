package com.sadcodes.employee.controller;

import com.sadcodes.employee.exception.MissingParameterException;
import com.sadcodes.employee.model.dto.EmployeeDto;
import com.sadcodes.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/save")
    public ResponseEntity<EmployeeDto> savedEmployee(@RequestBody EmployeeDto employeeDto) {
        return new ResponseEntity<>(employeeService.savedEmployeeDto(employeeDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeDto> updateEmployeeDto(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        return new ResponseEntity<>(employeeService.updateEmployeeDto(id, employeeDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getSingleEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(employeeService.getSingleEmployee(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @GetMapping("/get-by-emp-code-and-company-name")
    public ResponseEntity<EmployeeDto> getEmployeeByEmpCodeAndCompanyName(
            @RequestParam(required = false) String empCode, @RequestParam(required = false) String companyName
    ) {
        List<String >missingParameter = new ArrayList<>();
        if (empCode==null || empCode.trim().isEmpty()){
            missingParameter.add("empCode");
        }
        if (companyName==null || companyName.trim().isEmpty()){
            missingParameter.add("companyName");
        }
        if (!missingParameter.isEmpty()){
            String finalMessage = missingParameter.stream().collect(Collectors.joining(","));
            throw new MissingParameterException("Please provide: "+ finalMessage);
        }
        EmployeeDto response = employeeService.getEmployeeByEmpCodeAndCompanyName(empCode,companyName);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
