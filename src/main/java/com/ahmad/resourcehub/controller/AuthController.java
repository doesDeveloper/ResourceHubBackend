package com.ahmad.resourcehub.controller;

import com.ahmad.resourcehub.dto.AuthResponseDTO;
import com.ahmad.resourcehub.dto.UserLoginDTO;
import com.ahmad.resourcehub.dto.UserRegisterDTO;
import com.ahmad.resourcehub.model.User;
import com.ahmad.resourcehub.service.JwtService;
import com.ahmad.resourcehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        User user = userService.register(userRegisterDTO);
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        AuthResponseDTO response = new AuthResponseDTO(token, user.getUsername(), user.getFullName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(), userLoginDTO.getPassword()));
        User user = userService.findByUsername(userLoginDTO.getUsername());
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        AuthResponseDTO response = new AuthResponseDTO(token, user.getUsername(), user.getFullName());
        return ResponseEntity.ok(response);
    }
}

