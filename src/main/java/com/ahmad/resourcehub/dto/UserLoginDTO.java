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
@Schema(description = "Credentials for user authentication")
public class UserLoginDTO {

    @Schema(
            description = "The user's unique login ID (alphanumeric only)",
            example = "user123",
            minLength = 3,
            maxLength = 20,
            pattern = "^[a-zA-Z0-9]+$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    @Schema(
            description = "The user's password",
            example = "MySecretPass123!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "password" // This masks the input in Swagger UI
    )
    @NotEmpty(message = "Password is required")
    private String password;
}
