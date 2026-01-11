package com.ahmad.resourcehub.exception.file;

import com.ahmad.resourcehub.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FileReadException extends ApplicationException {
    public FileReadException(String message) {
        super(message, "FILE_READ_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
