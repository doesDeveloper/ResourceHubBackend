package com.ahmad.resourcehub.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@lombok.Data
@Getter
@Setter
@Builder
public class UserRegisterDTO {

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    @NotEmpty(message = "Username is required")
    private String username;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    @NotEmpty(message = "Email is required")
    private String email;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotEmpty(message = "Password is required")
    private String password;
    @NotEmpty(message = "Full name is required")
    private String fullName;
}
