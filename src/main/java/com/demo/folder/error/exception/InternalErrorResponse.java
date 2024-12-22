package com.demo.folder.error.exception;


import java.time.LocalDateTime;

public class InternalErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String details;

}
