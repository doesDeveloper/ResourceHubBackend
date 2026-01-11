package com.ahmad.resourcehub.dto;

import com.ahmad.resourcehub.model.Resource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@lombok.Data
@Getter
@Setter
@Builder
public class BreadcrumbElement {
    private UUID uuid;
    private String name;
    public static BreadcrumbElement from(Resource resource) {
        return BreadcrumbElement.builder()
                .uuid(resource.getUuid())
                .name(resource.getName())
                .build();
    }
}
