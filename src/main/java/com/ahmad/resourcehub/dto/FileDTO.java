package com.ahmad.resourcehub.dto;

import com.ahmad.resourcehub.model.FileMetadata;
import com.ahmad.resourcehub.model.Resource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
public class FileDTO {

    private UUID uuid;
    private String name;
    private LocalDateTime createdAt;
    private String description;
    private String filePath;
    private String uploadedBy;
    private Map<String, Integer> rates;
    private double ratings;
    private int ratingCount;
    @Builder.Default
    private Boolean bookmarked=false;

    public static FileDTO from(Resource resource, FileMetadata fileMetadata) {
        return FileDTO.builder()
                .uuid(resource.getUuid())
                .name(resource.getName())
                .createdAt(resource.getCreatedAt())
                .description(fileMetadata.getDescription())
                .filePath(fileMetadata.getFilePath())
                .uploadedBy(fileMetadata.getUploadedBy())
                .ratings(fileMetadata.getRating())
                .rates(fileMetadata.getRates())
                .build();
    }
}
