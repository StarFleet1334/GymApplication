package com.demo.folder.entity.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TrainerProfileResponseDTO {

    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeProfileResponseDTO> traineeList;

}