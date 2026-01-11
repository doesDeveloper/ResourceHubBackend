package com.ahmad.resourcehub.controller;

import com.ahmad.resourcehub.dto.BookmarkDTO;
import com.ahmad.resourcehub.dto.FileDTO;
import com.ahmad.resourcehub.dto.RateDTO;
import com.ahmad.resourcehub.repository.FileMetadataRepository;
import com.ahmad.resourcehub.service.ResourceService;
import com.ahmad.resourcehub.service.UserFeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserFeatureController {
    private final UserFeatureService userFeatureService;
    private final ResourceService resourceService;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileMetadataRepository metaRepository;

    @PostMapping("/bookmark")
    public ResponseEntity<?> toggleBookmark(@RequestBody @Valid BookmarkDTO bookmarkDTO, Principal principal) {
        List<UUID> bookmarks = userFeatureService.toggleBookmark(bookmarkDTO);
        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/bookmark")
    public ResponseEntity<List<FileDTO>> getBookmarks() {
        List<FileDTO> fileDTOS = userFeatureService.getBookmarks();
        return ResponseEntity.ok(fileDTOS);
    }

    @PostMapping("/rate")
    public ResponseEntity<FileDTO> rate(@RequestBody @Valid RateDTO rateDTO, Principal principal) {
        FileDTO dto = userFeatureService.addRating(rateDTO);
        return ResponseEntity.ok(dto);

    }

//    @GetMapping("/rate")
//    public ResponseEntity<?> getRating(@RequestParam(value = "uuid") UUID uuid, Principal principal) {
//        FileMetadata meta = metaRepository.findByUuid(uuid).orElse(null);
//        if (meta == null) return ResponseEntity.status(404).body("File not found");
//        //Temp patch
//        FileDTO fileInfo = resourceService.getFileInfo_Old(uuid);
//        Map<String, Integer> rates = meta.getRates();
//        if (rates == null) rates = new HashMap<>();
//        Map<String, Integer> rate = new HashMap<>();
//        if (rates.get(principal.getName()) == null)
//            rate.put(principal.getName(), 0);
//        else
//            rate.put(principal.getName(), rates.get(principal.getName()));
//        fileInfo.setRates(rate);
//        return ResponseEntity.status(200).body(fileInfo);
//
//    }
}
