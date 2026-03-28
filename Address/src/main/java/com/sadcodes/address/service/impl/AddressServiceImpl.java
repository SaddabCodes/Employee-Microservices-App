package com.sadcodes.address.service.impl;

import com.sadcodes.address.model.dto.AddressDto;
import com.sadcodes.address.model.entity.AddressRequest;
import com.sadcodes.address.repository.AddressRepository;
import com.sadcodes.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

   private final AddressRepository addressRepository;
   private final ModelMapper modelMapper;

    @Override
    public AddressDto savedAddress(AddressRequest addressRequest) {
        return null;
    }

    @Override
    public AddressDto updateAddress(AddressRequest addressRequest) {
        return null;
    }

    @Override
    public AddressDto getSingleAddress(Long id) {
        return null;
    }

    @Override
    public List<AddressDto> getAllAddress() {
        return List.of();
    }

    @Override
    public void deleteAddress(Long id) {

    }
}
