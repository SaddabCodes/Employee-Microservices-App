package com.sadcodes.address.controllere;

import com.sadcodes.address.model.dto.AddressDto;
import com.sadcodes.address.model.entity.AddressRequest;
import com.sadcodes.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update")
    public ResponseEntity<List<AddressDto>> updateAddress(@RequestBody AddressRequest addressDto){
        return new ResponseEntity<>(addressService.updateAddress(addressDto),HttpStatus.CREATED);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto>getSingleAddress(@PathVariable("addressId") Long id){
        return new ResponseEntity<>(addressService.getSingleAddress(id),HttpStatus.OK);
    }

    @GetMapping("/all-address")
    public ResponseEntity<List<AddressDto>>getAllAddress(){
        return new ResponseEntity<>(addressService.getAllAddress(),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<String>deleteAddress(@PathVariable("addressId") Long id){
        addressService.deleteAddress(id);
        return new ResponseEntity<>("Address deleted successful",HttpStatus.OK);
    }
}
