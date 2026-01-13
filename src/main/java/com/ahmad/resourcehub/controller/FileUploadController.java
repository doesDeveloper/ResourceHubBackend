package com.ahmad.resourcehub.controller;


import com.ahmad.resourcehub.dto.error.ApiErrorDTO;
import com.ahmad.resourcehub.exception.ResourceNotFoundException;
import com.ahmad.resourcehub.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files/")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "Endpoints for serving and uploading files.")
public class FileUploadController {

    private final FileStorageService fileStorageService;


//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(MultipartFile file) {
//        String filename = fileStorageService.storeFile(file);
//        String url = "/uploads/" + file.getOriginalFilename();
//        return ResponseEntity.ok("File uploaded successfully: " + url);
//    }

    @Operation(
            summary = "Download or View a file",
            description = "Retrieves a file from storage. Supports both inline viewing (for images/PDFs) and forced download via query parameter."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary") // <--- KEY: Tells Swagger this is a file
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found in storage",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @SecurityRequirements()
    @ResponseBody
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @Parameter(description = "The exact name of the file (including extension)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6.pdf")
            @PathVariable String filename,
            @Parameter(description = "Set to 'true' to force the browser to download the file. Defaults to 'false' (inline view).")
            @RequestParam(name = "download", required = false, defaultValue = "false") boolean download
    ) {

        Resource file = fileStorageService.loadAsResource(filename);

        if (file == null)
            throw new ResourceNotFoundException("note", filename);

        String contentType = download ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fileStorageService.getContentType(filename);

        HttpHeaders headers = new HttpHeaders();
        String dispositionType = download ? "attachment" : "inline";
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, dispositionType + "; filename=\"" + file.getFilename() + "\"");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("%s;filename=\"%s\"", dispositionType, file.getFilename()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .headers(headers)
                .body(file);

    }
}
