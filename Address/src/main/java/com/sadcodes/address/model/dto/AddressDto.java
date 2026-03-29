package com.sadcodes.address.model.dto;

import com.sadcodes.address.model.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

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
