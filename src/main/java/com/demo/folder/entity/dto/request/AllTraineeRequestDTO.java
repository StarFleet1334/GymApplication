package com.demo.folder.entity.dto.request;

import com.demo.folder.entity.dto.response.TraineeTrainingResponseDTO;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AllTraineeRequestDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<String> trainers;
    private List<TraineeTrainingResponseDTO> trainings;

}