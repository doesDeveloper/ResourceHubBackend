package com.ahmad.resourcehub.dto;

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
public class FileUploadDTO {

    @NotNull(message = "parentUUID is required")
    private UUID parentUUID;

    @NotEmpty(message = "File name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotEmpty(message = "Author is required")
    private String author;
}
