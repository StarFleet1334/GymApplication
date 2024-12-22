package com.demo.folder.entity.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTraineeProfileRequestDTO {

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @NotNull(message = "Is Active status is required")
    private Boolean isActive;

}
