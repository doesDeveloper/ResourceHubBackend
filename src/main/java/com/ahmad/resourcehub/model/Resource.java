package com.ahmad.resourcehub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resources")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    private String name;

    @Enumerated(EnumType.STRING)
    private ResourceType type; // FOLDER or FILE

    private UUID parentUuid;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean readOnly = false;

    @PrePersist
    public void onCreate() {
        uuid = UUID.randomUUID();
        createdAt = LocalDateTime.now();
    }


    public enum ResourceType {
        FOLDER, FILE
    }

}