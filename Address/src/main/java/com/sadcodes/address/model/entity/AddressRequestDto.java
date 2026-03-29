package com.sadcodes.address.model.entity;

import com.sadcodes.address.model.enums.AddressType;
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
public class AddressRequestDto {

    private Long id;
    private String street;
    private Long pinCode;
    private String  city;
    private String  country;
    private AddressType addressType;
}
