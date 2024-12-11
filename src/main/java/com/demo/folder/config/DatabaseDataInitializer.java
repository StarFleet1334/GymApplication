package com.demo.folder.config;

import com.demo.folder.entity.dto.request.CreateTraineeRequestDTO;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.repository.*;
import com.demo.folder.service.TraineeService;
import com.demo.folder.service.TrainerService;
import com.demo.folder.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component
public class DatabaseDataInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDataInitializer.class);

    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TrainingTypeService trainingTypeService;

    @PostConstruct
    public void initData() {
        LOGGER.info("Initializing database data...");

        TrainingTypeRequestDTO trainingType1 = new TrainingTypeRequestDTO();
        trainingType1.setTrainingTypeName("Box");

        TrainingTypeRequestDTO trainingType2 = new TrainingTypeRequestDTO();
        trainingType2.setTrainingTypeName("Karate");

        TrainingTypeRequestDTO trainingType3 = new TrainingTypeRequestDTO();
        trainingType3.setTrainingTypeName("Basketball");

        trainingTypeService.createTrainingType(trainingType1);
        trainingTypeService.createTrainingType(trainingType2);
        trainingTypeService.createTrainingType(trainingType3);

        TrainerRequestDTO trainer1 = new TrainerRequestDTO();
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setTrainingTypeId(1L);

        TrainerRequestDTO trainer2 = new TrainerRequestDTO();
        trainer2.setFirstName("Jane");
        trainer2.setLastName("Smith");
        trainer2.setTrainingTypeId(2L);

        TrainerRequestDTO trainer3 = new TrainerRequestDTO();
        trainer3.setFirstName("Sam");
        trainer3.setLastName("Brown");
        trainer3.setTrainingTypeId(2L);

        trainerService.create(trainer1);
        trainerService.create(trainer2);
        trainerService.create(trainer3);

        CreateTraineeRequestDTO trainee1 = new CreateTraineeRequestDTO();
        trainee1.setFirstName("Trainee1FirstName");
        trainee1.setLastName("Trainee1LastName");

        CreateTraineeRequestDTO trainee2 = new CreateTraineeRequestDTO();
        trainee2.setFirstName("Trainee2FirstName");
        trainee2.setLastName("Trainee2LastName");

        CreateTraineeRequestDTO trainee3 = new CreateTraineeRequestDTO();
        trainee3.setFirstName("Trainee3FirstName");
        trainee3.setLastName("Trainee3LastName");

        traineeService.create(trainee1);
        traineeService.create(trainee2);
        traineeService.create(trainee3);

    }
}
