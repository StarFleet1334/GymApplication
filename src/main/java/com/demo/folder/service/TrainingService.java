package com.demo.folder.service;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.dto.request.TrainingRequestDTO;
import com.demo.folder.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);
    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TraineeService traineeService;

    @Transactional
    public void saveTraining(Training training) {
        LOGGER.info("Saving training with name: {}", training.getTrainingName());
        trainingRepository.save(training);
    }

    @Transactional(readOnly = true)
    public List<Training> getAllTrainings() {
        LOGGER.info("Fetching all trainings");
        List<Training> trainings = trainingRepository.findAll();
        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("No trainings found.");
        }

        return trainings;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void createTraining(TrainingRequestDTO trainingRequestDTO) {
        int duration = trainingRequestDTO.getDuration().intValue();
        if (duration < 0) {
            throw new IllegalArgumentException("Training duration must not be negative.");
        }
        Trainee trainee = traineeService.findTraineeByUsername(trainingRequestDTO.getTraineeUserName());
        if (trainee == null) {
            throw new EntityNotFoundException(
                    "Trainee with username " + trainingRequestDTO.getTraineeUserName() + " not found.");
        }

        Trainer trainer = trainerService.findTrainerByUsername(trainingRequestDTO.getTrainerUserName());
        if (trainer == null) {
            throw new EntityNotFoundException(
                    "Trainer with username " + trainingRequestDTO.getTrainerUserName() + " not found.");
        }

        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
            traineeService.updateTrainee(trainee);
        }

        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
            trainerService.updateTrainer(trainer);
        }


        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingRequestDTO.getTrainingName());
        training.setTrainingDate(trainingRequestDTO.getTrainingDate());
        training.setTrainingDuration(trainingRequestDTO.getDuration());

        trainee.getTrainings().add(training);
        LOGGER.info("Trainee's trainings list: {}", trainee.getTrainings());
        traineeService.updateTrainee(trainee);

        saveTraining(training);
        LOGGER.info("Created training for trainee {} with trainer {}", trainee.getUser().getUsername(),
                trainer.getUser().getUsername());
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestDTO> retrieveAllTrainings() {
        List<Training> trainings = getAllTrainings();
        return trainings.stream()
                .map(training -> {
                    TrainingRequestDTO trainingRequestDTO = new TrainingRequestDTO();
                    trainingRequestDTO.setTraineeUserName(training.getTrainee().getUser().getUsername());
                    trainingRequestDTO.setTrainerUserName(training.getTrainer().getUser().getUsername());
                    trainingRequestDTO.setTrainingName(training.getTrainingName());
                    trainingRequestDTO.setTrainingDate(training.getTrainingDate());
                    trainingRequestDTO.setDuration(training.getTrainingDuration());
                    return trainingRequestDTO;
                })
                .collect(Collectors.toList());
    }
}
