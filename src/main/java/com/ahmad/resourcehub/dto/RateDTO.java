package com.ahmad.resourcehub.dto;

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
public class RateDTO {
    @NotNull(message = "UUID is required")
    private UUID uuid;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5.")
    @Max(value = 5, message = "Rating must be between 1 and 5.")
    private int rating;
}
