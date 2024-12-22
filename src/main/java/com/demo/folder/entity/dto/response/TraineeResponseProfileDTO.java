package com.demo.folder.entity.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TraineeResponseProfileDTO {

    private String firstName;
    private String lastName;
    private LocalDate date_of_birth;
    private String address;
    private boolean isActive;
    private List<TrainerResponseProfileDTO> trainerResponseProfileDTOList;
    private List<TraineeTrainingResponseDTO> trainings;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setTrainerProfileList(
            List<TrainerResponseProfileDTO> trainerResponseProfileDTOList) {
        this.trainerResponseProfileDTOList = trainerResponseProfileDTOList;
    }

}