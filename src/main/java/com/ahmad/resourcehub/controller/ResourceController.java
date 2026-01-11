package com.ahmad.resourcehub.controller;

import com.ahmad.resourcehub.dto.*;
import com.ahmad.resourcehub.model.Resource;
import com.ahmad.resourcehub.service.ResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/resources")
@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping("/")
    public List<ResourceDto> listChildren(@RequestParam(required = false) UUID parentUuid) {
        //TODO: Convert to Response Object
        List<Resource> resources = resourceService.getChildren(parentUuid);
        return resources.stream().map(ResourceDto::from).toList();
    }

    @GetMapping("/folder")
    public ResponseEntity<FolderDTO> getFolder(@RequestParam(required = true) UUID uuid) {
        FolderDTO folderDTO = resourceService.getFolderInfo(uuid);
        return ResponseEntity.ok(folderDTO);
    }

    @GetMapping("/file")
    public ResponseEntity<FileDTO> getFile(@RequestParam(required = true) UUID uuid, HttpServletRequest request) {
        FileDTO fileDTO = resourceService.getFileInfo(uuid);
        return ResponseEntity.ok(fileDTO);

    }


    // TODO: Make it response object
    @GetMapping("/breadcrumb")
    public ResponseEntity<List<BreadcrumbElement>> getBreadcrumb(@RequestParam(required = true) UUID uuid) {
        List<Resource> crumbs = resourceService.getBreadcrumb(uuid);
        return ResponseEntity.ok(crumbs.stream().map(BreadcrumbElement::from).toList());
    }


    @PostMapping("/create/folders")
    public ResponseEntity<ResourceDto> createFolder(@RequestBody @Valid CreateFolderRequest request) {
        Resource saved = resourceService.createFolder(request);
        return ResponseEntity.ok(ResourceDto.from(saved));
    }

    @PostMapping("/create/folders/super")
    public ResponseEntity<ResourceDto> createSuperFolder(@RequestBody @Valid SuperFolderRequest request) {
        Resource saved = resourceService.createSuperFolder(request);
        return ResponseEntity.ok(ResourceDto.from(saved));
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<?> deleteResource(@PathVariable UUID uuid) {
        resourceService.deleteResource(uuid);
        return ResponseEntity.ok("Successfully deleted");
    }


    @PostMapping("/create/files")
    public ResponseEntity<?> createFile(
            @RequestPart(value = "file", required = true) MultipartFile file,
            @ModelAttribute @Valid FileUploadDTO fileUploadDTO
    ) {
        FileDTO dto = resourceService.createFile(file, fileUploadDTO);
        return ResponseEntity.ok(dto);
    }
}

