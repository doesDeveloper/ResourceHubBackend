package com.ahmad.resourcehub.service;

import com.ahmad.resourcehub.dto.*;
import com.ahmad.resourcehub.exception.BadRequestException;
import com.ahmad.resourcehub.exception.BusinessValidationException;
import com.ahmad.resourcehub.exception.ForbiddenException;
import com.ahmad.resourcehub.exception.ResourceNotFoundException;
import com.ahmad.resourcehub.model.FileMetadata;
import com.ahmad.resourcehub.model.Resource;
import com.ahmad.resourcehub.repository.FileMetadataRepository;
import com.ahmad.resourcehub.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final FileMetadataRepository fileMetadataRepository;

    private final UserService userService;
    private final FileStorageService fileStorageService;

    public FileDTO getFileInfo_Old(UUID uuid) {
        Resource resource = resourceRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        if (resource.getType() != Resource.ResourceType.FILE)
            throw new RuntimeException("Resource not a file");
        FileMetadata meta = fileMetadataRepository
                .findByUuid(resource.getUuid())
                .orElseThrow(() -> new RuntimeException("File metadata not found"));
        return FileDTO.from(resource, meta);
    }

    public List<Resource> getChildren(UUID parentUuid) {
        return (parentUuid == null)
                ? resourceRepository.findByParentUuidIsNull()
                : resourceRepository.findByParentUuid(parentUuid);
    }

    public FolderDTO getFolderInfo(UUID uuid) {
        Resource resource = resourceRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("folder", uuid.toString()));
        if (resource.getType() != Resource.ResourceType.FOLDER)
            throw new BusinessValidationException("Resource with identifier %s is not a folder", "INVALID_ACCESS");
        List<Resource> resources = getChildren(uuid);
        List<ResourceDto> resourceDtos = resources.stream().map(ResourceDto::from).toList();
        return FolderDTO.builder()
                .uuid(resource.getUuid())
                .type(resource.getType())
                .name(resource.getName())
//                .readOnly(resource.getReadOnly())
                .children(resourceDtos)
                .build();
    }

    public FileDTO getFileInfo(UUID uuid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        FileMetadata meta = fileMetadataRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("FILE", uuid.toString()));
        Resource file = resourceRepository.findByUuid(uuid).orElseThrow(() -> new BusinessValidationException("You should never reach this error", "FILE_MISMATCH"));
        FileDTO fileDto = FileDTO.from(file, meta);
        Map<String, Integer> rates = meta.getRates();
        if (rates == null) rates = Collections.emptyMap();
        if (isAuthenticated(auth)) {
            String username = auth.getName();
            // Put user ratings
            fileDto.setRates(Map.of(username, rates.getOrDefault(username, 0)));

            // Put user bookmarks
            if (userService.findByUsername(auth.getName()).getBookmarks().contains(uuid))
                fileDto.setBookmarked(true);
        } else {
            fileDto.setRates(Collections.emptyMap());
        }
        fileDto.setRatingCount(rates.size());
        return fileDto;
    }

    public List<Resource> getBreadcrumb(UUID uuid) {
        List<Resource> crumbs = new ArrayList<>();
        if (uuid == null) return crumbs;
        Resource current = resourceRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("item", uuid.toString()));
        //Try to find a fix for infinite loop fix.
        while (current != null) {
            if (current.getType() == Resource.ResourceType.FOLDER)
                crumbs.add(current);
            if (current.getParentUuid() == null) break;
            current = resourceRepository.findByUuid(current.getParentUuid()).orElse(null);
        }
        Collections.reverse(crumbs);
        return crumbs;
    }

    public Resource createFolder(CreateFolderRequest request) {
        Resource folder = Resource.builder()
                .name(request.getName())
                .type(Resource.ResourceType.FOLDER)
                .parentUuid(request.getParentUUID())
                .build();

        return resourceRepository.save(folder);
    }

    public Resource createSuperFolder(SuperFolderRequest request) {
        Resource folder = Resource.builder()
                .name(request.getName())
                .type(Resource.ResourceType.FOLDER)
                .parentUuid(null)
                .build();
        return resourceRepository.save(folder);
    }

    public void deleteResource(UUID uuid) {
        Resource resource = resourceRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("item", uuid.toString()));
        log.info("Deleting item {} with name {}", uuid, resource.getName());
//        if (resource.getType() == Resource.ResourceType.FOLDER) {
//            List<Resource> childResources = resourceRepository.findByParentUuid(uuid);
//            childResources.forEach(child -> deleteResource(child.getUuid()));
//        } else {
//            deleteFileMeta(resource);
//            Now handled my SQL
//        }
//        Parent id now foreign key hence, auto delete children.
        resourceRepository.delete(resource);
    }


    public FileDTO createFile(MultipartFile file, FileUploadDTO fileUploadDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) throw new ForbiddenException("creation", "file");
        final int maxSize = 10485760; // 10 * 1024 * 1024
        log.info("File size: {}", file.getSize());
        if (file.getSize() > maxSize)
            throw new BusinessValidationException("File size must be less than 10MB", "FILE_SIZE_EXCEEDED");

        //PDF file only
        if (file.getContentType() != null && !file.getContentType().startsWith("application/pdf"))
            throw new BusinessValidationException("Only pdf files are allowed", "ILLEGAL_FILE_TYPE");

        Resource parent = resourceRepository
                .findByUuid(fileUploadDTO.getParentUUID())
                .orElseThrow(() -> new ResourceNotFoundException("parent-uuid", fileUploadDTO.getParentUUID().toString()));

        if (parent.getType() != Resource.ResourceType.FOLDER)
            throw new BadRequestException("ILLEGAL_PARENT_ACCESS");
        if (parent.getReadOnly())
            throw new BusinessValidationException("Parent folder is read-only", "READ_ONLY_PARENT");

        Resource resource = Resource.builder()
                .name(fileUploadDTO.getName())
                .type(Resource.ResourceType.FILE)
                .parentUuid(fileUploadDTO.getParentUUID())
                .build();
        resource = resourceRepository.save(resource);
        String url = fileStorageService.storeFile(file, resource.getUuid().toString());
        log.info("File with uuid {} has file path {}.", resource.getUuid(), url);
        FileMetadata fileMetadata = FileMetadata.builder()
                .uuid(resource.getUuid())
                .description(fileUploadDTO.getDescription())
                .filePath(url)
                .author(fileUploadDTO.getAuthor())
                .uploadedBy(auth.getName())
                .verified(false)
                .rating(0.0)
                .rates(Collections.emptyMap())
                .build();

        fileMetadataRepository.save(fileMetadata);
        return FileDTO.from(resource, fileMetadata);
    }

    // Util functions
    private void deleteFileMeta(Resource resource) {
        try {
            fileMetadataRepository.findByUuid(resource.getUuid()).ifPresent(metadata -> {
                        // get filename from path /uploads/{filename}
                        String filename = metadata.getFilePath()
                                .substring(metadata.getFilePath().lastIndexOf("/") + 1);
                        fileStorageService.deleteFile(filename);
                        fileMetadataRepository.delete(metadata);
                    }
            );
        } catch (Exception e) {
            throw new BusinessValidationException(e.getMessage(), "RESOURCE_DELETE_FAILED");
        }
    }

    private boolean isAuthenticated(Authentication auth) {
        return auth != null &&
                auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken);
    }
}
