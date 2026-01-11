package com.ahmad.resourcehub.service;

import com.ahmad.resourcehub.dto.BookmarkDTO;
import com.ahmad.resourcehub.dto.FileDTO;
import com.ahmad.resourcehub.dto.RateDTO;
import com.ahmad.resourcehub.exception.ForbiddenException;
import com.ahmad.resourcehub.exception.ResourceNotFoundException;
import com.ahmad.resourcehub.model.FileMetadata;
import com.ahmad.resourcehub.model.Resource;
import com.ahmad.resourcehub.model.User;
import com.ahmad.resourcehub.repository.FileMetadataRepository;
import com.ahmad.resourcehub.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFeatureService {
    private final FileMetadataRepository fileMetadataRepository;
    private final FileMetadataRepository metaRepository;
    private final ResourceRepository resourceRepository;
    private final UserService userService;
    private final ResourceService resourceService;

    public List<UUID> toggleBookmark(BookmarkDTO bookmarkDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) throw new ForbiddenException("bookmark", bookmarkDTO.getUuid().toString());
        UUID id = bookmarkDTO.getUuid();
        fileMetadataRepository.findByUuid(id).orElseThrow(() -> new ResourceNotFoundException("FILE", id.toString()));
        User user = userService.findByUsername(auth.getName());
        List<UUID> bookmarks = user.getBookmarks();
        if (bookmarks.contains(id)) {
            bookmarks.remove(id);
        } else {
            bookmarks.add(id);
        }
        user.setBookmarks(bookmarks);
        userService.saveUser(user);
        // convert to JSON before returning.
        return bookmarks;
    }

    public List<FileDTO> getBookmarks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) throw new ForbiddenException("Illegal access.");
        User user = userService.findByUsername(auth.getName());
        List<UUID> bookmarks = user.getBookmarks();
        List<Resource> resources = resourceRepository.findByUuidInAndType(bookmarks, Resource.ResourceType.FILE);
        List<FileMetadata> fileMetadataList = fileMetadataRepository.findByUuidIn(bookmarks);
        Map<UUID, Resource> resourceMap = resources.stream().collect(Collectors.toMap(Resource::getUuid, r -> r));
        Map<UUID, FileMetadata> metadataMap = fileMetadataList.stream().collect(Collectors.toMap(FileMetadata::getUuid, r -> r));
        List<FileDTO> result = new ArrayList<>();
        for (UUID uuid : resourceMap.keySet()) {
            Resource resource = resourceMap.get(uuid);
            FileMetadata metadata = metadataMap.get(uuid);
            if (metadata != null) result.add(FileDTO.from(resource, metadata));
        }
        return result;
    }

    public FileDTO addRating(RateDTO rateDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) throw new ForbiddenException("Illegal access.");
        FileMetadata metadata = fileMetadataRepository.findByUuid(rateDTO.getUuid()).orElseThrow(() -> new ResourceNotFoundException("FILE", rateDTO.getUuid().toString()));
        Resource resource = resourceRepository.findByUuid(rateDTO.getUuid()).orElseThrow(() -> new ResourceNotFoundException("FILE", rateDTO.getUuid().toString()));
        Map<String, Integer> rates = metadata.getRates();
        if (rates == null) {
            metadata.setRates(new HashMap<>());
            rates = metadata.getRates();
        }
        Integer oldRating = rates.get(auth.getName());
        int count = rates.size();
        double total = metadata.getRating() * count;
        if (oldRating != null) {
            total -= oldRating;
            count--;
        }
        total += rateDTO.getRating();
        count++;
        double newRating = total / count;
        newRating = Math.round(newRating * 100.0) / 100.0;

        rates.put(auth.getName(), rateDTO.getRating());
        metadata.setRating(newRating);
        metaRepository.save(metadata);
        return FileDTO.from(resource, metadata);
    }

    private boolean isAuthenticated(Authentication auth) {
        return auth != null &&
                auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken);
    }
}
