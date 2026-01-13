package com.ahmad.resourcehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response object containing authentication details")
public class AuthResponseDTO {
    @Schema(
            description = "JWT Access Token used for subsequent authorized requests",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZG9l... (truncated)"
    )
    private String token;

    @Schema(
            description = "Unique username of the authenticated user",
            example = "jdoe123"
    )
    private String username;

    @Schema(
            description = "Full legal name of the user",
            example = "John Doe"
    )
    private String fullName;
}
