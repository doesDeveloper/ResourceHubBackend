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
@Schema(description = "Request object for creating a new folder")
public class CreateFolderRequest {
    @Schema(
            description = "The display name of the folder",
            example = "Project Documents",
            minLength = 3,
            maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Name is required")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long")
    private String name;

    @Schema(
            description = "The UUID of the parent directory where this folder will be created",
            example = "c56a4180-65aa-42ec-a945-5fd21dec0538",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Parent UUID is required")
    private UUID parentUUID;
}
