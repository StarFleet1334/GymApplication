package com.demo.folder.service;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.AllTraineeRequestDTO;
import com.demo.folder.entity.dto.request.CreateTraineeRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeProfileRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeTrainersRequestDTO;
import com.demo.folder.entity.dto.response.*;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO.TrainerInfoDTO;
import com.demo.folder.mapper.TraineeMapper;
import com.demo.folder.repository.TraineeRepository;
import com.demo.folder.repository.TrainerRepository;
import com.demo.folder.repository.TrainingRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.folder.utils.EntityUtil.validateTraineeRequestDTO;
import static com.demo.folder.utils.Generator.generatePassword;

@Service
public class TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    @Autowired
    private TrainerService trainerService;
    private final TrainingRepository trainingRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private HttpSession session;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository, @Lazy PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        LOGGER.info("TraineeService initialized.");

    }

    @Transactional(readOnly = true)
    public Trainee findTraineeByUsername(String username) {
        LOGGER.info("Finding Trainee by username: {}", username);
        if (username == null || username.isEmpty()) {
            LOGGER.info("Username cannot be null or empty.");
        }

        Trainee trainee = traineeRepository.findByUsername(username);

        if (trainee == null) {
            LOGGER.info("Trainee with username {} not found.", username);
        }
        return trainee;
    }


    @Transactional
    public void updateTrainee(Trainee trainee) {
        String username = trainee.getUser().getUsername();
        LOGGER.info("Updating Trainee with username: {}", trainee.getUser().getUsername());
        Trainee existingTrainee = traineeRepository.findByUsername(username);
        if (existingTrainee == null) {
            throw new EntityNotFoundException("Trainee with username " + username + " not found.");
        }
        traineeRepository.save(trainee);
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TraineeResponse createTrainee(CreateTraineeRequestDTO traineeRequestDTO) {
        validateTraineeRequestDTO(traineeRequestDTO);

        LOGGER.info("Creating new Trainee with first name: {}", traineeRequestDTO.getFirstName());
        String plainTextPassword = generatePassword();
        Trainee trainee = TraineeMapper.INSTANCE.toEntity(traineeRequestDTO);
        User user = createUser(traineeRequestDTO, plainTextPassword);
        trainee.setUser(user);
        LOGGER.info("Creating new Trainee with username: {}", trainee.getUser().getUsername());
        traineeRepository.save(trainee);
        LOGGER.debug("Trainee created: {}", trainee);
        FileUtil.writeCredentialsToFile("trainee_credentials.txt", user.getUsername(), plainTextPassword);
        String token = jwtTokenUtil.generateToken(user.getUsername(),"TRAINEE");
        session.setAttribute("TOKEN", token);
        session.setAttribute("USERNAME", user.getUsername());
        return TraineeMapper.INSTANCE.toResponse(trainee, token);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateTraineeTrainers(String traineeUserName,
                                      UpdateTraineeTrainersRequestDTO requestDTO,
                                      boolean add) {
        Trainee trainee = traineeRepository.findByUsername(traineeUserName);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee with username " + traineeUserName + " not found.");
        }

        List<Trainer> trainers = requestDTO.getTrainerUsernames().stream()
                .map(this::findTrainerByUsername)
                .toList();

        trainers.forEach(trainer -> {
            if (add) {
                trainee.getTrainers().add(trainer);
                trainer.getTrainees().add(trainee);
            } else {
                trainee.getTrainers().remove(trainer);
                trainer.getTrainees().remove(trainee);
            }
            updateTrainee(trainee);
            trainerService.updateTrainer(trainer);
        });

    }


    @Transactional
    public Trainee updateTraineeProfile(String traineeUserName,
                                        UpdateTraineeProfileRequestDTO requestDTO) {

        Trainee trainee = findTraineeByUsername(traineeUserName);
        User user = trainee.getUser();
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setUsername(traineeUserName);
        user.setActive(requestDTO.getIsActive());

        if (requestDTO.getDateOfBirth() != null) {
            trainee.setDateOfBirth(requestDTO.getDateOfBirth());
        }
        if (requestDTO.getAddress() != null) {
            trainee.setAddress(requestDTO.getAddress());
        }
        updateTrainee(trainee);
        return trainee;

    }

    @Transactional(readOnly = true)
    public UpdateTraineeTrainersResponseDTO getUnassignedActiveTrainers(String username) {
        Trainee trainee = traineeRepository.findByUsername(username);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee with username " + username + " not found.");
        }

        List<Trainer> allTrainers = trainerService.getAllTrainers();

        List<Trainer> assignedTrainers = trainee.getTrainers();

        List<Trainer> unassignedActiveTrainers = allTrainers.stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .filter(x -> x.getUser().isActive())
                .toList();

        List<TrainerInfoDTO> trainerInfoDTOS = unassignedActiveTrainers.stream()
                .map(trainer -> {
                    TrainerInfoDTO dto = new TrainerInfoDTO();
                    dto.setFirstName(trainer.getUser().getFirstName());
                    dto.setLastName(trainer.getUser().getLastName());
                    dto.setUsername(trainer.getUser().getUsername());
                    dto.setTrainerSpecializationId(trainer.getSpecialization().getId());
                    return dto;
                })
                .collect(Collectors.toList());
        UpdateTraineeTrainersResponseDTO responseDTO = new UpdateTraineeTrainersResponseDTO();
        responseDTO.setTrainers(trainerInfoDTOS);
        return responseDTO;

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteTraineeById(Long traineeId) {
        LOGGER.info("Deleting Trainee with ID: {}", traineeId);

        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with ID " + traineeId + " not found"));

        List<Training> trainings = trainingRepository.findByTrainee(trainee);
        trainingRepository.deleteAll(trainings);

        for (Trainer trainer : trainee.getTrainers()) {
            trainer.getTrainees().remove(trainee);
            trainerRepository.save(trainer);
        }

        trainee.getTrainers().clear();

        traineeRepository.deleteById(traineeId);
    }

    @Transactional(readOnly = true)
    public List<AllTraineeRequestDTO> getAllTraineesDetails() {
        LOGGER.info("Fetching all Trainees.");
        List<Trainee> trainees = traineeRepository.findAll();
        if (trainees.isEmpty()) {
            throw new EntityNotFoundException("No trainees found.");
        }
        List<AllTraineeRequestDTO> dtos = new ArrayList<>();

        for (Trainee trainee : trainees) {
            AllTraineeRequestDTO dto = new AllTraineeRequestDTO();
            dto.setId(trainee.getId());
            dto.setFirstName(trainee.getUser().getFirstName());
            dto.setLastName(trainee.getUser().getLastName());
            dto.setUsername(trainee.getUser().getUsername());
            dto.setPassword(trainee.getUser().getPassword());
            dto.setAddress(trainee.getAddress());
            dto.setDateOfBirth(trainee.getDateOfBirth());
            dto.setActive(trainee.getUser().isActive());

            List<String> trainerUsernames = new ArrayList<>();
            for (Trainer trainer : trainee.getTrainers()) {
                trainerUsernames.add(trainer.getUser().getUsername());
            }
            dto.setTrainers(trainerUsernames);

            List<TraineeTrainingResponseDTO> trainingResponseDTOS = trainee.getTrainings().stream()
                    .map(training -> {
                        TraineeTrainingResponseDTO trainingDTO = new TraineeTrainingResponseDTO();
                        trainingDTO.setTrainerName(training.getTrainer().getUser().getUsername());
                        trainingDTO.setTrainingName(training.getTrainingName());
                        trainingDTO.setTrainingType(training.getTrainer().getSpecialization().getTrainingTypeName());
                        trainingDTO.setTrainingDuration(training.getTrainingDuration());
                        trainingDTO.setTrainingDate(training.getTrainingDate());
                        return trainingDTO;
                    })
                    .collect(Collectors.toList());
            dto.setTrainings(trainingResponseDTOS);

            dtos.add(dto);
        }
        return dtos;
    }


    @Transactional(readOnly = true)
    public TraineeResponseProfileDTO getTraineeProfileByUsername(String username)
            throws EntityNotFoundException {
        Trainee trainee = findTraineeByUsername(username);
        TraineeResponseProfileDTO traineeResponseProfileDTO = new TraineeResponseProfileDTO();
        traineeResponseProfileDTO.setFirstName(trainee.getUser().getFirstName());
        traineeResponseProfileDTO.setLastName(trainee.getUser().getLastName());
        traineeResponseProfileDTO.setDate_of_birth(trainee.getDateOfBirth());
        traineeResponseProfileDTO.setAddress(trainee.getAddress());
        traineeResponseProfileDTO.setActive(trainee.getUser().isActive());

        List<Training> trainings = trainee.getTrainings();
        if (!trainings.isEmpty()) {
            List<TraineeTrainingResponseDTO> traineeTrainingResponseDTOS = trainings.stream()
                    .map(training -> {
                        TraineeTrainingResponseDTO traineeTrainingResponseDTO = new TraineeTrainingResponseDTO();
                        traineeTrainingResponseDTO.setTrainerName(training.getTrainer().getUser().getUsername());
                        traineeTrainingResponseDTO.setTrainingName(training.getTrainingName());
                        traineeTrainingResponseDTO.setTrainingType(training.getTrainer().getSpecialization().getTrainingTypeName());
                        traineeTrainingResponseDTO.setTrainingDuration(training.getTrainingDuration());
                        traineeTrainingResponseDTO.setTrainingDate(training.getTrainingDate());
                        return traineeTrainingResponseDTO;
                    })
                    .collect(Collectors.toList());
            traineeResponseProfileDTO.setTrainings(traineeTrainingResponseDTOS);
        }

        List<TrainerResponseProfileDTO> trainerResponseProfileDTOList = getTrainerProfiles(trainee);
        traineeResponseProfileDTO.setTrainerProfileList(trainerResponseProfileDTOList);
        return traineeResponseProfileDTO;
    }

    private static List<TrainerResponseProfileDTO> getTrainerProfiles(Trainee trainee) {
        return trainee.getTrainers().stream()
                .map(trainer -> {
                    TrainerResponseProfileDTO trainerResponseProfileDTO = new TrainerResponseProfileDTO();
                    trainerResponseProfileDTO.setFirstName(trainer.getUser().getFirstName());
                    trainerResponseProfileDTO.setLastName(trainer.getUser().getLastName());
                    trainerResponseProfileDTO.setUserName(trainer.getUser().getUsername());
                    trainerResponseProfileDTO.setTrainingType(trainer.getSpecialization().getTrainingTypeName());
                    return trainerResponseProfileDTO;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public AllTraineeRequestDTO updateTrainee(String username,
                                              UpdateTraineeProfileRequestDTO requestDTO) throws EntityNotFoundException {

        Trainee updateTraineeProfile = updateTraineeProfile(username, requestDTO);

        AllTraineeRequestDTO allTraineeRequestDTO = new AllTraineeRequestDTO();
        allTraineeRequestDTO.setFirstName(updateTraineeProfile.getUser().getFirstName());
        allTraineeRequestDTO.setLastName(updateTraineeProfile.getUser().getLastName());
        allTraineeRequestDTO.setUsername(updateTraineeProfile.getUser().getUsername());
        allTraineeRequestDTO.setDateOfBirth(updateTraineeProfile.getDateOfBirth());
        allTraineeRequestDTO.setAddress(updateTraineeProfile.getAddress());
        allTraineeRequestDTO.setActive(updateTraineeProfile.getUser().isActive());

        List<String> trainersUserNames = updateTraineeProfile.getTrainers().stream()
                .map(trainer -> trainer.getUser().getUsername())
                .collect(Collectors.toList());
        allTraineeRequestDTO.setTrainers(trainersUserNames);
        return allTraineeRequestDTO;
    }


    @Transactional(readOnly = true)
    public List<TraineeTrainingResponseDTO> getFilteredTrainings(String username, LocalDate periodFrom,
                                                                 LocalDate periodTo, String trainingName, String trainingType) throws EntityNotFoundException {
        Trainee trainee = findTraineeByUsername(username);
        if (trainee == null) {
            throw new EntityNotFoundException("Trainee with username " + username + " not found.");
        }

        return trainee.getTrainings().stream()
                .filter(training -> periodFrom == null || !training.getTrainingDate().isBefore(periodFrom))
                .filter(training -> {
                    LocalDate calculatedPeriodTo = addDurationToTrainingDate(training.getTrainingDate(),
                            training.getTrainingDuration());
                    return periodTo == null || !calculatedPeriodTo.isAfter(periodTo);
                })
                .filter(training -> trainingName == null || training.getTrainingName()
                        .equalsIgnoreCase(trainingName))
                .filter(training -> trainingType == null || training.getTrainer().getSpecialization()
                        .getTrainingTypeName().equalsIgnoreCase(trainingType))
                .map(EntityUtil::mapToTraineeTrainingResponseDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public void modifyTraineeState(String username,boolean state) {
        Trainee trainee = findTraineeByUsername(username);
        if (trainee == null) {
            throw new EntityNotFoundException("Trainee with given username not found");
        }

        if (trainee.getUser().isActive() && state) {
            throw new IllegalArgumentException("Trainee is already activated");
        }

        if (!trainee.getUser().isActive() && !state) {
            throw new IllegalArgumentException("Trainee is already activated");
        }
        if (state) {
            LOGGER.info("Activating Trainee with ID: {}", trainee.getId());
            traineeRepository.updateTraineeStatus(trainee.getId(), true);
        } else {
            LOGGER.info("Deactivating Trainee with ID: {}", trainee.getId());
            traineeRepository.updateTraineeStatus(trainee.getId(), false);
        }
    }

    private User createUser(CreateTraineeRequestDTO requestDTO, String plainTextPassword) {
        User user = TraineeMapper.INSTANCE.mapToUser(requestDTO);
        user.setUsername(Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
        user.setPassword(passwordEncoder.encode(plainTextPassword));
        user.setActive(true);
        return user;
    }

    private LocalDate addDurationToTrainingDate(LocalDate trainingDate, Number trainingDurationInMinutes) {
        return trainingDate.plusDays(trainingDurationInMinutes.intValue() / (24 * 60));
    }

    private Trainer findTrainerByUsername(String trainerUsername) {
        Trainer trainer = trainerRepository.findByUsername(trainerUsername);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer with username " + trainerUsername + " not found.");
        }
        return trainer;
    }


}