package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ChangeLoginRequestDTO extends LoginRequestDTO {

    @NotBlank(message = "New Password is required")
    private String newPassword;

    public @NotBlank(message = "New Password is required") String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(
            @NotBlank(message = "New Password is required") String newPassword) {
        this.newPassword = newPassword;
    }
}