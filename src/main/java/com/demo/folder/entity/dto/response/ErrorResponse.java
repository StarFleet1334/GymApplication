package com.demo.folder.entity.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private String message;
    private List<String> details;

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

}
