package com.demo.folder.entity.dto.response;

import java.util.List;

public class UpdateTraineeTrainersResponseDTO {

    private List<TrainerInfoDTO> trainers;

    public static class TrainerInfoDTO {

        private String username;
        private String firstName;
        private String lastName;
        private Long trainerSpecializationId;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Long getTrainerSpecializationId() {
            return trainerSpecializationId;
        }

        public void setTrainerSpecializationId(Long trainerSpecializationId) {
            this.trainerSpecializationId = trainerSpecializationId;
        }
    }

    // Getters and Setters
    public List<TrainerInfoDTO> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerInfoDTO> trainers) {
        this.trainers = trainers;
    }


}