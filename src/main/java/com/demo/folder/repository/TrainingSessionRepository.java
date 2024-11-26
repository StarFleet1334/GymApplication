package com.demo.folder.repository;

import com.demo.folder.entity.base.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    Optional<TrainingSession> findByTrainerUserNameAndTrainerFirstNameAndTrainerLastNameAndTrainingDateAndTrainingDuration(
            String trainerUserName, String trainerFirstName, String trainerLastName, LocalDate trainingDate, Integer trainingDuration);
}
