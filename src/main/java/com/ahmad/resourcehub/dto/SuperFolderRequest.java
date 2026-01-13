package com.ahmad.resourcehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Request to create a top-level super folder")
public class SuperFolderRequest {
    @Schema(
            description = "The display name of the super folder",
            example = "Executive Records",
            minLength = 3,
            maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Name is required")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long")
    private String name;
}

