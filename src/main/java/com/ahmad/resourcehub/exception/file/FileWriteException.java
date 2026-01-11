package com.ahmad.resourcehub.exception.file;

import com.ahmad.resourcehub.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FileWriteException extends ApplicationException {
    public FileWriteException(String message, String errorCode){
        super(message,errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
