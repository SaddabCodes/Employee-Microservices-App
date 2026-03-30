package com.sadcodes.address.service;

import com.sadcodes.address.model.dto.AddressDto;
import com.sadcodes.address.model.entity.AddressRequest;

import java.util.List;

public interface AddressService {
    List<AddressDto> savedAddress(AddressRequest addressRequest);
    List<AddressDto> updateAddress(AddressRequest addressRequest);
    AddressDto getSingleAddress(Long id);
    List<AddressDto>getAllAddress();
    void deleteAddress(Long id);
}
