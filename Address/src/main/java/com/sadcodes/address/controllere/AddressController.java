package com.sadcodes.address.controllere;

import com.sadcodes.address.model.dto.AddressDto;
import com.sadcodes.address.model.entity.AddressRequest;
import com.sadcodes.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/save")
    public ResponseEntity<List<AddressDto>>savedAddress(@RequestBody AddressRequest addressDto){
        return new ResponseEntity<>(addressService.savedAddress(addressDto), HttpStatus.CREATED);
    }

}
