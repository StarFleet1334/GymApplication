package com.demo.folder.entity.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UpdateTraineeTrainersResponseDTO {

    private List<TrainerInfoDTO> trainers;

    @Data
    public static class TrainerInfoDTO {

        private String username;
        private String firstName;
        private String lastName;
        private Long trainerSpecializationId;

    }

}