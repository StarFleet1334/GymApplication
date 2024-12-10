package com.demo.folder.service;

import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.base.TrainingType;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTrainerProfileRequestDTO;
import com.demo.folder.entity.dto.response.TrainerProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerResponseDTO;
import com.demo.folder.entity.dto.response.TrainerTrainingResponseDTO;
import com.demo.folder.mapper.TrainerMapper;
import com.demo.folder.repository.TrainerRepository;
import com.demo.folder.repository.TrainingTypeRepository;
import com.demo.folder.utils.EntityUtil;
import com.demo.folder.utils.FileUtil;
import com.demo.folder.utils.Generator;
import com.demo.folder.utils.JwtTokenUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.folder.utils.EntityUtil.*;

@Service
public class TrainerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);
    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private HttpSession session;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          TrainingTypeRepository trainingTypeRepository, @Lazy PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public void create(TrainerRequestDTO trainerRequestDTO) {
        validateTrainerName(trainerRequestDTO);
        String plainTextPassword = Generator.generatePassword();
        TrainingType trainingType = getTrainingType(trainerRequestDTO.getTrainingTypeId());
        User user = createUser(trainerRequestDTO, plainTextPassword,passwordEncoder);
        Trainer trainer = TrainerMapper.INSTANCE.toEntity(trainerRequestDTO);
        trainer.setSpecialization(trainingType);
        trainer.setUser(user);
        trainerRepository.save(trainer);
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        validateTrainerName(trainerRequestDTO);
        LOGGER.info("Creating new Trainer with first name: {}", trainerRequestDTO.getFirstName());
        String plainTextPassword = Generator.generatePassword();
        TrainingType trainingType = getTrainingType(trainerRequestDTO.getTrainingTypeId());
        if (trainingType == null) {
            return null;
        }
        User user = createUser(trainerRequestDTO, plainTextPassword,passwordEncoder);
        Trainer trainer = TrainerMapper.INSTANCE.toEntity(trainerRequestDTO);
        trainer.setSpecialization(trainingType);
        trainer.setUser(user);
        trainerRepository.save(trainer);
        FileUtil.writeCredentialsToFile("trainer_credentials.txt", user.getUsername(), plainTextPassword);
        String token = jwtTokenUtil.generateToken(user.getUsername(),"TRAINER");
        session.setAttribute("TOKEN", token);
        session.setAttribute("USERNAME", user.getUsername());
        return TrainerMapper.INSTANCE.toResponse(trainer,token);
    }


    @Transactional(readOnly = true)
    public Trainer findTrainerByUsername(String username) {
        LOGGER.info("Finding Trainer by username: {}", username);
        if (username == null || username.isEmpty()) {
            LOGGER.info("Username cannot be null or empty.");
        }

        Trainer trainer = trainerRepository.findByUsername(username);
        if (trainer == null) {
            LOGGER.info("Trainer with username {} not found.", username);
        }
        return trainer;
    }



    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        LOGGER.info("Fetching all Trainers.");
        List<Trainer> trainers = trainerRepository.findAll();

        if (trainers.isEmpty()) {
            throw new EntityNotFoundException("No trainers found.");
        }
        return trainers;
    }

    @Transactional
    public void updateTrainer(Trainer trainer) {
        String username = trainer.getUser().getUsername();
        LOGGER.info("Updating Trainer with username: {}", trainer.getUser().getUsername());
        Trainer existingTrainer = trainerRepository.findByUsername(username);
        if (existingTrainer == null) {
            throw new EntityNotFoundException("Trainer with username " + username + " not found.");
        }
        trainerRepository.save(trainer);
    }


    @Transactional(readOnly = true)
    public TrainerProfileResponseDTO getTrainerProfile(String userName) {
        Trainer trainer = findTrainerByUsername(userName);
        if (trainer == null) {
            throw new EntityNotFoundException("Trainer with username " + userName + " not found.");
        }
        return createTrainerProfileResponseDTO(trainer);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Trainer updateTrainerProfile(String trainerUserName,
                                        UpdateTrainerProfileRequestDTO requestDTO) {
        Trainer trainer = findTrainerByUsername(trainerUserName);
        if (trainer == null) {
            throw new EntityNotFoundException("Trainer with username " + trainerUserName + " not found.");
        }
        updateUserInfo(trainer.getUser(), requestDTO);
        TrainingType type = findOrCreateTrainingType(requestDTO.getSpecialization());
        trainer.setSpecialization(type);
        trainerRepository.save(trainer);
        return trainer;

    }


    @Transactional(readOnly = true)
    public List<TrainerRequestDTO> retrieveAllTrainers() {
        List<Trainer> trainers = getAllTrainers();
        return trainers.stream().map(EntityUtil::convertToTrainerRequestDTO).collect(Collectors.toList());
    }


    @Transactional
    public TrainerProfileResponseDTO updateTrainer(String username,
                                                   UpdateTrainerProfileRequestDTO requestDTO) throws EntityNotFoundException {
        Trainer trainer = updateTrainerProfile(username, requestDTO);
        return createTrainerProfileResponseDTO(trainer);
    }

    @Transactional(readOnly = true)
    public List<TrainerTrainingResponseDTO> getFilteredTrainingsForTrainer(String username,
                                                                           LocalDate periodFrom, LocalDate periodTo, String traineeName) throws EntityNotFoundException {
        Trainer trainer = findTrainerByUsername(username);
        if (trainer == null) {
            throw new com.demo.folder.error.exception.EntityNotFoundException(
                    "Trainer with username " + username + " not found.");
        }

        List<Training> trainings = trainer.getTrainings();

        List<Training> filteredTrainings = trainings.stream()
                .filter(training -> periodFrom == null || !training.getTrainingDate().isBefore(periodFrom))
                .filter(training -> {
                    LocalDate calculatedPeriodTo = addDurationToTrainingDate(training.getTrainingDate(),
                            training.getTrainingDuration());
                    return periodTo == null || !calculatedPeriodTo.isAfter(periodTo);
                })
                .filter(training -> traineeName == null || training.getTrainee().getUser().getUsername()
                        .equalsIgnoreCase(traineeName))
                .toList();

        return filteredTrainings.stream()
                .map(training -> {
                    TrainerTrainingResponseDTO traineeTrainingDTO = new TrainerTrainingResponseDTO();
                    traineeTrainingDTO.setTraineeName(training.getTrainer().getUser().getUsername());
                    traineeTrainingDTO.setTrainingName(training.getTrainingName());
                    traineeTrainingDTO.setTrainingType(
                            training.getTrainer().getSpecialization().getTrainingTypeName());
                    traineeTrainingDTO.setTrainingDuration(training.getTrainingDuration());
                    traineeTrainingDTO.setTrainingDate(training.getTrainingDate());
                    return traineeTrainingDTO;
                }).toList();
    }

    @Transactional
    public void modifyTrainerState(String username,boolean state) {
        Trainer trainer = findTrainerByUsername(username);
        if (trainer == null) {
            throw new EntityNotFoundException("Trainer with given username not found");
        }

        if (trainer.getUser().isActive() && state) {
            throw new IllegalArgumentException("Trainer is already activated");
        }

        if (!trainer.getUser().isActive() && !state) {
            throw new IllegalArgumentException("Trainer is already activated");
        }
        if (state) {
            LOGGER.info("Activating Trainer with ID: {}", trainer.getId());
            trainerRepository.updateTrainerStatus(trainer.getId(), true);
        } else {
            LOGGER.info("Deactivating Trainer with ID: {}", trainer.getId());
            trainerRepository.updateTrainerStatus(trainer.getId(), false);
        }
    }


    private LocalDate addDurationToTrainingDate(LocalDate trainingDate, Number trainingDurationInMinutes) {
        return trainingDate.plusDays(trainingDurationInMinutes.intValue() / (24 * 60));
    }

    private TrainingType getTrainingType(Long trainingTypeId) {
        return trainingTypeRepository.findById(trainingTypeId).orElse(null);
    }

    private void updateUserInfo(User user, UpdateTrainerProfileRequestDTO requestDTO) {
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setUsername(Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
        user.setActive(requestDTO.isActive());
    }

    private TrainingType findOrCreateTrainingType(String specialization) {
        TrainingType type = trainingTypeRepository.findByName(specialization);
        if (type == null) {
            type = new TrainingType();
            type.setTrainingTypeName(specialization);
            trainingTypeRepository.save(type);
        }
        return type;
    }






}