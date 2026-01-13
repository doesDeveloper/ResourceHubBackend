package com.ahmad.resourcehub.controller;

import com.ahmad.resourcehub.dto.*;
import com.ahmad.resourcehub.dto.error.ApiErrorDTO;
import com.ahmad.resourcehub.model.Resource;
import com.ahmad.resourcehub.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/resources")
@RestController
@RequiredArgsConstructor
@Tag(name = "Resources Management", description = "Endpoints for fetching, creating, deleting, updating files and folders and other resource interactions.")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(summary = "List contents of a folder", description = "Get all child resources (files and folders) under a specific parent. If parentUuid is omitted, lists root resources.")
    @SecurityRequirements()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResourceDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid UUID provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @GetMapping("/")
    public List<ResourceDto> listChildren(@Parameter(description = "UUID of the parent folder. Leave empty for root.", example = "550e8400-e29b-41d4-a716-446655440000")
                                          @RequestParam(required = false) UUID parentUuid) {
        //TODO: Convert to Response Object
        List<Resource> resources = resourceService.getChildren(parentUuid);
        return resources.stream().map(ResourceDto::from).toList();
    }

    @Operation(summary = "Get Folder Details", description = "Retrieves metadata for a specific folder.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Folder found", content = @Content(schema = @Schema(implementation = FolderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Folder not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "422", description = "Resource not a folder", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))

    })
    @GetMapping("/folder")
    public ResponseEntity<FolderDTO> getFolder(@RequestParam(required = true) UUID uuid) {
        FolderDTO folderDTO = resourceService.getFolderInfo(uuid);
        return ResponseEntity.ok(folderDTO);
    }

    @Operation(summary = "Get File Details", description = "Retrieves metadata for a specific file.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File found", content = @Content(schema = @Schema(implementation = FileDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access Denied (Insufficient permissions)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth") // Assuming you named your scheme "bearerAuth" in OpenApiConfig
    @GetMapping("/file")
    public ResponseEntity<FileDTO> getFile(@RequestParam(required = true) UUID uuid, HttpServletRequest request) {
        FileDTO fileDTO = resourceService.getFileInfo(uuid);
        return ResponseEntity.ok(fileDTO);

    }


    // TODO: Make it response object
    @Operation(summary = "Get Breadcrumb Navigation", description = "Returns the path from root to the specified resource, useful for UI navigation bars.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Breadcrumb path retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BreadcrumbElement.class)))),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @GetMapping("/breadcrumb")
    public ResponseEntity<List<BreadcrumbElement>> getBreadcrumb(@RequestParam(required = true) UUID uuid) {
        List<Resource> crumbs = resourceService.getBreadcrumb(uuid);
        return ResponseEntity.ok(crumbs.stream().map(BreadcrumbElement::from).toList());
    }


    @Operation(summary = "Create a Sub-folder", description = "Creates a new folder inside an existing parent folder.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Folder created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid folder name or parent UUID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Only ADMIN or MODERATOR can create folders", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create/folders")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResourceDto> createFolder(@RequestBody @Valid CreateFolderRequest request) {
        Resource saved = resourceService.createFolder(request);
        return ResponseEntity.ok(ResourceDto.from(saved));
    }

    @Operation(summary = "Create a Root Folder", description = "Creates a top-level folder that has no parent. Only ADMINs access.")
    @PostMapping("/create/folders/super")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceDto> createSuperFolder(@RequestBody @Valid SuperFolderRequest request) {
        Resource saved = resourceService.createSuperFolder(request);
        return ResponseEntity.ok(ResourceDto.from(saved));
    }

    @Operation(summary = "Delete a Resource", description = "Permanently removes a file or folder. Restricted to ADMINs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Requires ADMIN role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource UUID not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteResource(@PathVariable UUID uuid) {
        resourceService.deleteResource(uuid);
        return ResponseEntity.ok("Successfully deleted");
    }


    @Operation(summary = "Upload a File", description = "Uploads a file along with metadata (description, parent folder, etc.).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Illegal parent uuid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Parent resource not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "422", description = "Invalid file size or file type.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "413", description = "File size exceeds limit", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create/files")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<FileDTO> createFile(
            @Parameter(description = "MultipartFile type. The binary file to upload.", required = true)
            @RequestPart(value = "file", required = true) MultipartFile file,

            @Parameter(description = "Metadata for the file (JSON or Form fields)", required = true)
            @ModelAttribute @Valid FileUploadDTO fileUploadDTO
    ) {
        FileDTO dto = resourceService.createFile(file, fileUploadDTO);
        return ResponseEntity.ok(dto);
    }
}

