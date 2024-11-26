package com.demo.folder.service;

import com.demo.folder.client.SecondaryMicroserviceClient;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.TrainingSession;
import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import com.demo.folder.repository.TrainingSessionRepository;
import com.demo.folder.utils.ActionType;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import Feign Exception handling if needed
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingSessionService.class);

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SecondaryMicroserviceClient secondaryMicroserviceClient;

//    public TrainingSession processTraining(TrainingSessionDTO dto, ActionType actionType) {
//        if (actionType == ActionType.ADD) {
//            LOGGER.info("Processing ADD action");
//            Trainer trainer = trainerService.findTrainerByUsername(dto.getTrainerUserName());
//            if (trainer == null) {
//                throw new EntityNotFoundException(
//                        "Trainer with username " + dto.getTrainerUserName() + " not found.");
//            }
//            if (!dto.getTrainerFirstName().equals(trainer.getUser().getFirstName()) ||
//                    !dto.getTrainerLastName().equals(trainer.getUser().getLastName())) {
//                LOGGER.error("Trainer's name does not match: provided first name: {}, last name: {}",
//                        dto.getTrainerFirstName(), dto.getTrainerLastName());
//                throw new IllegalArgumentException("Trainer's first name or last name does not match.");
//            }
//            if (dto.getTrainingDuration() == null || dto.getTrainingDuration().doubleValue() <= 0) {
//                throw new IllegalArgumentException("Training duration must be positive.");
//            }
//            if (dto.getTrainingDate() == null || dto.getTrainingDate().isBefore(LocalDate.now())) {
//                LOGGER.error("Invalid training date: {}. Training date must not be before today's date.", dto.getTrainingDate());
//                throw new IllegalArgumentException("Training date must not be before today's date.");
//            }
//            TrainingSession trainingSession = modelMapper.map(dto, TrainingSession.class);
//            trainingSessionRepository.save(trainingSession);
//
//            try {
//                secondaryMicroserviceClient.trainingAdded(dto);
//                LOGGER.info("Notified Secondary Microservice about training addition.");
//            } catch (Exception e) {
//                LOGGER.error("Failed to notify Secondary Microservice: {}", e.getMessage());
//            }
//
//            return trainingSession;
//        } else if (actionType == ActionType.DELETE) {
//            LOGGER.info("Processing DELETE action for Trainer: {}, Date: {}", dto.getTrainerUserName(), dto.getTrainingDate());
//            Optional<TrainingSession> session = trainingSessionRepository.findByTrainerUserNameAndTrainerFirstNameAndTrainerLastNameAndTrainingDateAndTrainingDuration(
//                    dto.getTrainerUserName(), dto.getTrainerFirstName(), dto.getTrainerLastName(), dto.getTrainingDate(), dto.getTrainingDuration().intValue());
//            if (session.isEmpty()) {
//                throw new EntityNotFoundException("No training session found matching the exact criteria.");
//            }
//            trainingSessionRepository.delete(session.get());
//            LOGGER.info("Deleted training session for Trainer: {}, Date: {}", dto.getTrainerUserName(), dto.getTrainingDate());
//
//            try {
//                secondaryMicroserviceClient.trainingDeleted(dto);
//                LOGGER.info("Notified Secondary Microservice about training deletion.");
//            } catch (Exception e) {
//                LOGGER.error("Failed to notify Secondary Microservice: {}", e.getMessage());
//
//            }
//
//            return null;
//        } else {
//            throw new IllegalArgumentException("Invalid action type");
//        }
//    }

    /**
     * Creates a new training session.
     *
     * @param dto The training session data transfer object.
     * @return The created training session.
     */
    public TrainingSession createTrainingSession(TrainingSessionDTO dto) {
        LOGGER.info("Creating new training session");

        Trainer trainer = trainerService.findTrainerByUsername(dto.getTrainerUserName());
        if (trainer == null) {
            throw new EntityNotFoundException(
                    "Trainer with username " + dto.getTrainerUserName() + " not found.");
        }

        if (!dto.getTrainerFirstName().equals(trainer.getUser().getFirstName()) ||
                !dto.getTrainerLastName().equals(trainer.getUser().getLastName())) {
            LOGGER.error("Trainer's name does not match: provided first name: {}, last name: {}",
                    dto.getTrainerFirstName(), dto.getTrainerLastName());
            throw new IllegalArgumentException("Trainer's first name or last name does not match.");
        }

        if (dto.getTrainingDuration() == null || dto.getTrainingDuration().doubleValue() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive.");
        }

        if (dto.getTrainingDate() == null || dto.getTrainingDate().isBefore(LocalDate.now())) {
            LOGGER.error("Invalid training date: {}. Training date must not be before today's date.", dto.getTrainingDate());
            throw new IllegalArgumentException("Training date must not be before today's date.");
        }

        TrainingSession trainingSession = modelMapper.map(dto, TrainingSession.class);
        trainingSessionRepository.save(trainingSession);
        LOGGER.info("Saved new training session with ID: {}", trainingSession.getId());

        try {
            secondaryMicroserviceClient.addTraining(dto);
            LOGGER.info("Notified Secondary Microservice about training addition.");
        } catch (Exception e) {
            LOGGER.error("Failed to notify Secondary Microservice: {}", e.getMessage());
        }

        return trainingSession;
    }

    /**
     * Deletes an existing training session by ID.
     *
     * @param id The ID of the training session to delete.
     */
    public void deleteTrainingSession(Long id) {
        LOGGER.info("Deleting training session with ID: {}", id);

        Optional<TrainingSession> sessionOptional = trainingSessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new EntityNotFoundException("Training session with ID " + id + " not found.");
        }

        TrainingSession trainingSession = sessionOptional.get();

        trainingSessionRepository.delete(trainingSession);
        LOGGER.info("Deleted training session with ID: {}", id);

        TrainingSessionDTO dto = modelMapper.map(trainingSession, TrainingSessionDTO.class);

        try {
            secondaryMicroserviceClient.deleteTraining(dto);
            LOGGER.info("Notified Secondary Microservice about training deletion.");
        } catch (Exception e) {
            LOGGER.error("Failed to notify Secondary Microservice: {}", e.getMessage());
        }
    }

    /**
     * Retrieves all training sessions.
     *
     * @return A list of all training sessions.
     */
    public List<TrainingSession> getAllTrainingSessions() {
        return trainingSessionRepository.findAll();
    }
}
