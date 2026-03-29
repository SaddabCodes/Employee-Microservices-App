package com.sadcodes.address.service.impl;

import com.sadcodes.address.model.dto.AddressDto;
import com.sadcodes.address.model.entity.Address;
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
    public List<AddressDto> savedAddress(AddressRequest addressRequest) {
        // TODO -> check if employee already exist
        List<Address> listToSave = addressRequest.getAddressRequestDtoList()
                .stream()
                .map(dto -> {
                    Address address = modelMapper.map(dto, Address.class);
                    address.setEmpId(addressRequest.getEmpId());
                    return address;
                })
                .toList();
        List<Address> savedAddress = addressRepository.saveAll(listToSave);
        return savedAddress.
                stream()
                .map(dto -> modelMapper.map(dto, AddressDto.class))
                .toList();
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
