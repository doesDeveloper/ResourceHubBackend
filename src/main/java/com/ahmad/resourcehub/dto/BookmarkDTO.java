package com.ahmad.resourcehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
@Schema(description = "Data transfer object for bookmarking resources")
public class BookmarkDTO {
    @Schema(
            description = "The unique UUID of the item to be bookmarked",
            example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "UUID is required")
    private UUID uuid;
}
