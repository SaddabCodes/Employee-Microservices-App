package com.sadcodes.employee.repository;

import com.sadcodes.employee.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findByEmpCodeAndCompanyName(String empCode, String companyName);
}
