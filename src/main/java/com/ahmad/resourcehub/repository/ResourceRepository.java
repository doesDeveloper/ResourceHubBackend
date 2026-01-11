package com.ahmad.resourcehub.repository;

import com.ahmad.resourcehub.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findById(Long id);
    Optional<Resource> findByUuid(UUID uuid);
    List<Resource> findByParentUuidIsNull();
    List<Resource> findByParentUuid(UUID parentUuid);
    List<Resource> findByUuidInAndType(List<UUID> uuids, Resource.ResourceType type);
}
