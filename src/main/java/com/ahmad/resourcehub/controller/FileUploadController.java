package com.ahmad.resourcehub.controller;


import com.ahmad.resourcehub.exception.ResourceNotFoundException;
import com.ahmad.resourcehub.service.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files/")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;


//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(MultipartFile file) {
//        String filename = fileStorageService.storeFile(file);
//        String url = "/uploads/" + file.getOriginalFilename();
//        return ResponseEntity.ok("File uploaded successfully: " + url);
//    }

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable String filename,
            @RequestParam(name = "download", required = false, defaultValue = "false") boolean download
    ) {

        Resource file = fileStorageService.loadAsResource(filename);

        if (file == null)
            throw new ResourceNotFoundException("note", filename);

        String contentType = download ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fileStorageService.getContentType(filename);

        HttpHeaders headers = new HttpHeaders();
        String dispositionType = download ? "attachment" : "inline";
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, dispositionType + "; filename=\"" + file.getFilename() + "\"");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("%s;filename=\"%s\"", dispositionType, file.getFilename()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .headers(headers)
                .body(file);

    }
}
