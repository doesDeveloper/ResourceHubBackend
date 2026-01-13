package com.ahmad.resourcehub.controller;

import com.ahmad.resourcehub.dto.BookmarkDTO;
import com.ahmad.resourcehub.dto.FileDTO;
import com.ahmad.resourcehub.dto.RateDTO;
import com.ahmad.resourcehub.dto.error.ApiErrorDTO;
import com.ahmad.resourcehub.service.UserFeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Engagement", description = "Endpoints for extra features provided to each user.")
public class UserFeatureController {
    private final UserFeatureService userFeatureService;

    @Operation(
            summary = "Toggle Bookmark",
            description = "Adds a resource to bookmarks if not present, or removes it if already present. Returns the updated list of bookmarked UUIDs."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bookmark successfully toggled",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "string", format = "uuid")))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid resource UUID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/bookmark")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<List<UUID>> toggleBookmark(@RequestBody @Valid BookmarkDTO bookmarkDTO) {
        List<UUID> bookmarks = userFeatureService.toggleBookmark(bookmarkDTO);
        return ResponseEntity.ok(bookmarks);
    }

    @Operation(
            summary = "Get My Bookmarks",
            description = "Retrieves the full file details for all resources bookmarked by the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved bookmarks",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FileDTO.class)))
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/bookmark")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<List<FileDTO>> getBookmarks() {
        List<FileDTO> fileDTOS = userFeatureService.getBookmarks();
        return ResponseEntity.ok(fileDTOS);
    }

    @Operation(
            summary = "Rate a File",
            description = "Submit a rating (e.g., 1-5 stars) for a file. Updates the file's average rating immediately."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rating submitted successfully",
                    content = @Content(schema = @Schema(implementation = FileDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid rating value (e.g., out of range)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/rate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<FileDTO> rate(@RequestBody @Valid RateDTO rateDTO, Principal principal) {
        FileDTO dto = userFeatureService.addRating(rateDTO);
        return ResponseEntity.ok(dto);

    }

}
