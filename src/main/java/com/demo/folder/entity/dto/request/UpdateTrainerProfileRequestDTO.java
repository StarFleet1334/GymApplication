package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTrainerProfileRequestDTO {

    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;

    private String specialization;

    @NotNull(message = "isActive is required")
    private boolean isActive;


    public @NotBlank(message = "firstName is required") String getFirstName() {
        return firstName;
    }

    public void setFirstName(
            @NotBlank(message = "firstName is required") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "lastName is required") String getLastName() {
        return lastName;
    }

    public void setLastName(
            @NotBlank(message = "lastName is required") String lastName) {
        this.lastName = lastName;
    }

    @NotNull(message = "isActive is required")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(
            @NotNull(message = "isActive is required") boolean active) {
        isActive = active;
    }
}