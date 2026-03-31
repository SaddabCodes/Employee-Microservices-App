package com.sadcodes.employee.client;

import com.sadcodes.employee.model.dto.AddressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ADDRESS")
public interface AddressClient {

    @GetMapping("/addresses/empId/{empId}")
    List<AddressDto> getAddressByEmpId(@PathVariable Long empId);
}
