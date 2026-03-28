package com.sadcodes.address.model.dto;

import com.sadcodes.address.model.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long empId;
    private String street;
    private String pinCode;
    private Long city;
    private Long country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
}
