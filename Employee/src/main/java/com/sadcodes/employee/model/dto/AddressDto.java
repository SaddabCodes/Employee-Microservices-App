package com.sadcodes.employee.model.dto;


import com.sadcodes.employee.model.enums.AddressType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private Long id;
    private Long empId;
    private String street;
    private Long pinCode;
    private String  city;
    private String  country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
}
