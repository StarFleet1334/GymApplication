package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTraineeTrainersRequestDTO {

    @NotEmpty(message = "Trainers list cannot be empty")
    private List<@NotBlank(message = "Trainer Username is required") String> trainerUsernames;

}