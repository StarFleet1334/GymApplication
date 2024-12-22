package com.demo.folder.error.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternalErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String details;

}
