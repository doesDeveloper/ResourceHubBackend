package com.ahmad.resourcehub.controller;

import com.ahmad.resourcehub.dto.AuthResponseDTO;
import com.ahmad.resourcehub.dto.UserLoginDTO;
import com.ahmad.resourcehub.dto.UserRegisterDTO;
import com.ahmad.resourcehub.dto.error.ApiErrorDTO;
import com.ahmad.resourcehub.model.User;
import com.ahmad.resourcehub.service.JwtService;
import com.ahmad.resourcehub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication Management", description = "Endpoints for user registration and login.")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT token for immediate access."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully registered",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed (e.g., username already exists or password too weak)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @SecurityRequirements()
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        User user = userService.registerUser(userRegisterDTO);
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        AuthResponseDTO response = new AuthResponseDTO(token, user.getUsername(), user.getFullName());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Authenticate user",
            description = "Validates credentials and returns a JWT token. Use this token in the 'Authorization' header for protected resources."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input format (e.g., missing fields)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials (wrong username or password)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @SecurityRequirements()
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

