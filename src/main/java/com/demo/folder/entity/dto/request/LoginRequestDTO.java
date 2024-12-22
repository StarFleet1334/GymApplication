package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "UserName is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

}