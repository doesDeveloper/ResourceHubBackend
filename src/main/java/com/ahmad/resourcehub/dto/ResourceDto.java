package com.ahmad.resourcehub.dto;

import com.ahmad.resourcehub.model.Resource;
import com.ahmad.resourcehub.model.Resource.ResourceType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
public class ResourceDto {
    private UUID uuid;
    private ResourceType type; // FOLDER or FILE
    private String name;
//    private Boolean readOnly;
    private LocalDateTime createdAt;

    public static ResourceDto from(Resource resource) {
        return ResourceDto.builder()
                .uuid(resource.getUuid())
                .type(resource.getType())
                .name(resource.getName())
//                .readOnly(resource.getReadOnly())
                .createdAt(resource.getCreatedAt())
                .build();
    }
}
