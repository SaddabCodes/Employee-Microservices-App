package com.sadcodes.auth.cotroller;

import com.sadcodes.auth.model.JwtTokenResponse;
import com.sadcodes.auth.model.LoginRequest;
import com.sadcodes.auth.model.UserDto;
import com.sadcodes.auth.model.UserEntity;
import com.sadcodes.auth.service.UserService;
import com.sadcodes.auth.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserEntity userEntity) {
        return new ResponseEntity<>(userService.savedUser(userEntity), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        String token = jwtUtil.generateToken(authentication);
        JwtTokenResponse tokenResponse = new JwtTokenResponse();
        tokenResponse.setToken(token);
        tokenResponse.setType("Bearer");
        tokenResponse.setValidUntil(Instant.ofEpochMilli(jwtUtil.getExpirationDateFromToken(token).getTime()).toString());

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication authentication) {
        return ResponseEntity.ok("Authenticated as " + authentication.getName());
    }
}
