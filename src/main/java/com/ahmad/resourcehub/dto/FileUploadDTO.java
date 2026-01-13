package com.ahmad.resourcehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
@Schema(description = "Metadata required for uploading a new file")
public class FileUploadDTO {

    @Schema(
            description = "The UUID of the directory where the file will be stored",
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "parentUUID is required")
    private UUID parentUUID;

    @Schema(
            description = "The logical name of the file (excluding extension if handled separately)",
            example = "Quarterly_Report_2024",
            minLength = 3,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "File name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Schema(
            description = "A brief summary of the file contents",
            example = "Financial report for Q1 2024 showing growth metrics.",
            minLength = 10,
            maxLength = 500
    )
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Schema(
            description = "The name or ID of the user uploading the file",
            example = "Alice Smith",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Author is required")
    private String author;
}
