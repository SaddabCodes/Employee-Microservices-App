package com.sadcodes.address.model.entity;

import com.sadcodes.address.model.enums.AddressType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long empId;
    private String street;
    private Long pinCode;
    private String  city;
    private String  country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
}
