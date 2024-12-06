package com.demo.folder.service;

import com.demo.folder.client.SecondaryMicroserviceClient;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.TrainingSession;
import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import com.demo.folder.repository.TrainingSessionRepository;
import com.demo.folder.transaction.TransactionIdHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public AtomicBoolean fallbackCalled = new AtomicBoolean(false);

    /**
     * Creates a new training session.
     *
     * @param dto The training session data transfer object.
     * @return The created training session.
     */
    @CircuitBreaker(name = "trainingSessionService", fallbackMethod = "fallbackCreateTrainingSession")
    public TrainingSession createTrainingSession(TrainingSessionDTO dto) {
        LOGGER.info("Creating new training session");
        String transactionId = TransactionIdHolder.getTransactionId();

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

        dto.setAction("add");
        secondaryMicroserviceClient.handleTraining(dto,transactionId);
        LOGGER.info("Notified Secondary Microservice about training addition.");
        return trainingSession;
    }

    /**
     * Deletes an existing training session by ID.
     *
     * @param id The ID of the training session to delete.
     */
    @CircuitBreaker(name = "trainingSessionService", fallbackMethod = "fallbackDeleteTrainingSession")
    public void deleteTrainingSession(Long id) {
        LOGGER.info("Deleting training session with ID: {}", id);
        String transactionId = TransactionIdHolder.getTransactionId();

        Optional<TrainingSession> sessionOptional = trainingSessionRepository.findById(id);
        if (sessionOptional.isEmpty()) {
            throw new EntityNotFoundException("Training session with ID " + id + " not found.");
        }

        TrainingSession trainingSession = sessionOptional.get();

        trainingSessionRepository.delete(trainingSession);
        LOGGER.info("Deleted training session with ID: {}", id);

        TrainingSessionDTO dto = modelMapper.map(trainingSession, TrainingSessionDTO.class);

        dto.setAction("delete");
        secondaryMicroserviceClient.handleTraining(dto,transactionId);
        LOGGER.info("Notified Secondary Microservice about training deletion.");

    }

    /**
     * Retrieves all training sessions.
     *
     * @return A list of all training sessions.
     */
    public List<TrainingSession> getAllTrainingSessions() {
        return trainingSessionRepository.findAll();
    }

    /**
     * Fallback method for createTrainingSession.
     *
     * @param dto       The training session data transfer object.
     * @param throwable The exception that triggered the fallback.
     * @return A default TrainingSession instance.
     */
    public TrainingSession fallbackCreateTrainingSession(TrainingSessionDTO dto, Throwable throwable) {
        String transactionId = TransactionIdHolder.getTransactionId();
        LOGGER.error("Fallback method called due to: {}", throwable.getMessage());
        dto.setTrainerFirstName("Fallback");
        dto.setTrainerLastName("Trainer");
        dto.setTrainerUserName("FallbackTrainer");
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(10);
        dto.setIsActive(false);
        dto.setAction("add");
        secondaryMicroserviceClient.handleTraining(dto,transactionId);
        // Since this is a fallback creation of training session, in memory it is not saved
        // same principle applies to delete until some task appears that may contain such thing
        // potentially cascading could be applied also
        return new TrainingSession();
    }


    /**
     * Fallback method for deleteTrainingSession.
     *
     * @param id        The ID of the training session to delete.
     * @param throwable The exception that triggered the fallback.
     */
    public void fallbackDeleteTrainingSession(Long id, Throwable throwable) {
        LOGGER.error("Fallback method for deleteTrainingSession called due to: {}", throwable.getMessage());
        fallbackCalled.set(true);
    }

    public boolean isFallbackCalled() {
        return fallbackCalled.get();
    }

    public void setFallbackCalled(boolean value) {
        fallbackCalled.set(value);
    }

    public void resetFallbackFlag() {
        fallbackCalled.set(false);
    }
}
