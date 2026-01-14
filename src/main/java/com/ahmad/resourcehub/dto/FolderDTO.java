package com.ahmad.resourcehub.dto;

import com.ahmad.resourcehub.model.Resource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
public class FolderDTO {
    private UUID uuid;
    private Resource.ResourceType type; // FOLDER Always
    private String name;
//    private Boolean readOnly;
    private List<ResourceDto> children;
}
