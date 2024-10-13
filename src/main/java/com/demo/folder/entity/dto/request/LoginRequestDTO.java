package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "UserName is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

    public @NotBlank(message = "UserName is required") String getUsername() {
        return username;
    }

    public void setUsername(
            @NotBlank(message = "UserName is required") String username) {
        this.username = username;
    }

    public @NotBlank(message = "Password is required") String getPassword() {
        return password;
    }

    public void setPassword(
            @NotBlank(message = "Password is required") String password) {
        this.password = password;
    }
}