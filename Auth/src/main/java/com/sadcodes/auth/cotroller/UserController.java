package com.sadcodes.auth.cotroller;

import com.sadcodes.auth.exception.BadRequestException;
import com.sadcodes.auth.model.JwtTokenResponse;
import com.sadcodes.auth.model.LoginRequest;
import com.sadcodes.auth.model.UserDto;
import com.sadcodes.auth.model.UserEntity;
import com.sadcodes.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register-user")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserEntity userEntity) {
        return new ResponseEntity<>(userService.savedUser(userEntity), HttpStatus.CREATED);
    }


    @PostMapping("/generate-token")
    public JwtTokenResponse generateToken(@RequestBody LoginRequest loginRequest){
        Authentication authenticate = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (authenticate.isAuthenticated()){
            return userService.generateToken(loginRequest.getUsername());
        }else {
            throw new BadRequestException("Invalid Credential");
        }
    }

}
