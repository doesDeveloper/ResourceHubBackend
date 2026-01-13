package com.ahmad.resourcehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
@Schema(description = "Data transfer object for submitting a rating")
public class RateDTO {
    @Schema(
            description = "The UUID of the item being rated",
            example = "a2d1645e-8b12-4d22-b5e5-3c46d5c6b7e8",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "UUID is required")
    private UUID uuid;

    @Schema(
            description = "The rating score from 1 to 5",
            example = "5",
            minimum = "1",
            maximum = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5.")
    @Max(value = 5, message = "Rating must be between 1 and 5.")
    private int rating;
}
