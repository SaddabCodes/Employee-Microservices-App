package com.sadcodes.address.service.impl;

import com.sadcodes.address.client.EmployeeClient;
import com.sadcodes.address.exception.BadRequestException;
import com.sadcodes.address.exception.ResourceNotFoundException;
import com.sadcodes.address.model.dto.AddressDto;
import com.sadcodes.address.model.dto.EmployeeDto;
import com.sadcodes.address.model.entity.Address;
import com.sadcodes.address.model.entity.AddressRequest;
import com.sadcodes.address.repository.AddressRepository;
import com.sadcodes.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final EmployeeClient employeeClient;

    @Override
    public List<AddressDto> savedAddress(AddressRequest addressRequest) {
        // TODO -> check if employee already exist
        EmployeeDto employee = employeeClient.getSingleEmployee(addressRequest.getEmpId());

        List<Address> listToSave = this.savedOrUpdateAddressRequest(addressRequest);
        List<Address> savedAddress = addressRepository.saveAll(listToSave);
        return savedAddress.
                stream()
                .map(dto -> modelMapper.map(dto, AddressDto.class))
                .toList();
    }

    @Override
    public List<AddressDto> updateAddress(AddressRequest addressRequest) {

        EmployeeDto employee = employeeClient.getSingleEmployee(addressRequest.getEmpId());

        List<Address> addressesByEmpId = addressRepository.findAllByEmpId(addressRequest.getEmpId());

        if (addressesByEmpId.isEmpty()) {
            logger.info("No address found for employee id {}", addressRequest.getEmpId());
        }

        List<Address> listToUpdate = this.savedOrUpdateAddressRequest(addressRequest);

        Set<Long> upcomingIds = listToUpdate.stream()
                .map(Address::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> existingIds = addressesByEmpId.stream()
                .map(Address::getId)
                .toList();

        List<Long> idsToDelete = existingIds.stream()
                .filter(id -> !upcomingIds.contains(id))
                .toList();

        if (!idsToDelete.isEmpty()) {
            addressRepository.deleteAllById(idsToDelete);
        }

        List<Address> updatedAddress = addressRepository.saveAll(listToUpdate);

        return updatedAddress.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public AddressDto getSingleAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAllAddress() throws InterruptedException {
        Thread.sleep(6000);
        List<Address> allAddress = addressRepository.findAll();
        if (allAddress.isEmpty()) {
            throw new ResourceNotFoundException("No address found");
        }
        return allAddress.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        addressRepository.delete(address);
    }

    @Override
    public List<AddressDto> getAddressByEmpId(Long empId) {
        List<Address> addressByEmpId = addressRepository.findAllByEmpId(empId);
        return addressByEmpId.stream()
                .map((address) -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    private List<Address> savedOrUpdateAddressRequest(AddressRequest addressRequest) {

        return addressRequest.getAddressRequestDtoList()
                .stream()
                .map(dto -> {

                    Address address;

                    if (dto.getId() != null) {
                        // 🔥 UPDATE CASE
                        address = addressRepository.findById(dto.getId())
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Address not found with id: " + dto.getId())
                                );

                        // update existing entity
                        modelMapper.map(dto, address);

                    } else {
                        // 🔥 CREATE CASE
                        address = modelMapper.map(dto, Address.class);
                    }

                    address.setEmpId(addressRequest.getEmpId());
                    return address;
                })
                .toList();
    }
}
