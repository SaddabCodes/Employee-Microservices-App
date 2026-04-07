package com.sadcodes.auth.service;

import com.sadcodes.auth.model.JwtTokenResponse;
import com.sadcodes.auth.model.UserDto;
import com.sadcodes.auth.model.UserEntity;
import com.sadcodes.auth.repository.UserRepository;
import com.sadcodes.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserDto savedUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity savedUser = userRepository.save(userEntity);
        return new UserDto(savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRoles());
    }

    public JwtTokenResponse generateToken(String username){
        String token = jwtUtil.generateToken(username);
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
        jwtTokenResponse.setToken(token);
        jwtTokenResponse.setType("Bearer");
        jwtTokenResponse.setValidUntil(jwtUtil.getExpirationDateFromToken(token).toString());
        return jwtTokenResponse;
    }
}
