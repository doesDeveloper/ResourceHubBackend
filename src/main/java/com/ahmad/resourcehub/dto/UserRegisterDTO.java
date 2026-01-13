package com.ahmad.resourcehub.dto;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data required to register a new user")
public class UserRegisterDTO {
    @Schema(
            description = "Desired username (alphanumeric only)",
            example = "johnDoe99",
            minLength = 3,
            maxLength = 20,
            pattern = "^[a-zA-Z0-9]+$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    @NotEmpty(message = "Username is required")
    private String username;

    @Schema(
            description = "A valid email address for notifications",
            example = "john.doe@example.com",
            format = "email", // Helps UI tools validate input type
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    @NotEmpty(message = "Email is required")
    private String email;

    @Schema(
            description = "Password (must be at least 8 characters)",
            example = "SecureP@ssw0rd!",
            minLength = 8,
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "password" // Masks input in Swagger UI
    )
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotEmpty(message = "Password is required")
    private String password;

    @Schema(
            description = "The user's legal full name",
            example = "John Doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Full name is required")
    private String fullName;
}
