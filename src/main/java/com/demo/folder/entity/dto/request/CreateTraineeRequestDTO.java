package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTraineeRequestDTO {
    @NotBlank(message = "First Name is required")
    private String firstName;
    @NotBlank(message = "Last Name is required")
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;


    public @NotBlank(message = "First Name is required") String getFirstName() {
        return firstName;
    }

    public void setFirstName(
            @NotBlank(message = "First Name is required") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "Last Name is required") String getLastName() {
        return lastName;
    }

    public void setLastName(
            @NotBlank(message = "Last Name is required") String lastName) {
        this.lastName = lastName;
    }

}