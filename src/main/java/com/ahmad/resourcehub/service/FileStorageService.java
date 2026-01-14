package com.ahmad.resourcehub.service;

import com.ahmad.resourcehub.exception.ForbiddenException;
import com.ahmad.resourcehub.exception.file.FileReadException;
import com.ahmad.resourcehub.exception.file.FileWriteException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@Service
public class FileStorageService {
    private final Cloudinary cloudinary;

    public FileStorageService(
            @Value("${cloudinary.url}") String cloudinaryUrl
    ) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }


    public String storeFile(MultipartFile file, String filename) {
        try {
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
            filename = filename.concat(extension);
//            Path targetLocation = this.uploadDir.resolve(filename).normalize();
//            if (!targetLocation.getParent().equals(uploadDir)) {
//                return null;
//            }
//            Files.copy(file.getInputStream(), targetLocation, REPLACE_EXISTING);
            Map params = ObjectUtils.asMap(
                    "resource_type", "raw",
                    "use_filename", true,
                    "unique_filename", true,
                    "public_id", filename);
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            System.out.println(uploadResult);
//            return "/uploads/" + filename;
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new FileWriteException(e.getMessage(), "FILE_WRITE_FAILED");
        }
    }

    public String storeFile(MultipartFile file) {
        return storeFile(file, file.getOriginalFilename());
    }

    public boolean deleteFile(String filename) {
        try {
            Path targetLocation = this.uploadDir.resolve(filename).normalize();
            if (targetLocation.toFile().exists())
                return Files.deleteIfExists(targetLocation);
            return false;
        } catch (IOException e) {
            throw new FileWriteException(e.getMessage(), "FILE_DELETE_FAILED");
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = this.uploadDir.resolve(filename).normalize();
            if (!file.getParent().equals(uploadDir)) {
                throw new ForbiddenException("access", filename);
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileReadException(String.format("File %s may not exist.", filename));
            }
        } catch (MalformedURLException e) {
            throw new FileReadException(e.getMessage());
        }
    }

    public String getContentType(String filename) {
        try {
            String contentType = Files.probeContentType(this.uploadDir.resolve(filename).normalize());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return contentType;
        } catch (IOException e) {
            throw new FileReadException(e.getMessage());
        }
    }

    // Get Extension
    public String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

}
