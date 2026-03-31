package com.sadcodes.employee.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    Long id;
    String empName;
    String empEmail;
    String empCode;
    String companyName;
    private List<AddressDto> addressDto;
}