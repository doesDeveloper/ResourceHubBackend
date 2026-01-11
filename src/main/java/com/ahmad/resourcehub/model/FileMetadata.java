package com.ahmad.resourcehub.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {
    @Id
    private UUID uuid;
    private String description;
    private String filePath;
    @Builder.Default
    private String author="";
    private String uploadedBy;
    private Boolean verified;
    @Builder.Default
    private double rating =0;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="rates_collection", joinColumns=@JoinColumn(name="uuid"))
    private Map<String, Integer> rates = new HashMap<>();
}
