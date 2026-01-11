package com.ahmad.resourcehub.repository;

import com.ahmad.resourcehub.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
    Optional<FileMetadata> findByUuid(UUID uuid);
    List<FileMetadata> findByUuidIn(List<UUID> uuids);
}
