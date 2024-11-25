package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTraineeTrainersRequestDTO {

    @NotEmpty(message = "Trainers list cannot be empty")
    private List<@NotBlank(message = "Trainer Username is required") String> trainerUsernames;

    public List<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(List<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}