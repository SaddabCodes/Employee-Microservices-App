package com.sadcodes.auth.cotroller;

import com.sadcodes.auth.model.UserDto;
import com.sadcodes.auth.model.UserEntity;
import com.sadcodes.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto>registerUser(@RequestBody UserEntity userEntity){
        return new ResponseEntity<>(userService.savedUser(userEntity), HttpStatus.CREATED);
    }
}
