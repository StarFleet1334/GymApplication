package com.demo.folder.mapper;

import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.dto.request.TrainingRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "duration", target = "trainingDuration")
    Training toEntity(TrainingRequestDTO trainingRequestDTO);

    @Mapping(source = "trainee.user.username", target = "traineeUserName")
    @Mapping(source = "trainer.user.username", target = "trainerUserName")
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDuration", target = "duration")
    TrainingRequestDTO toDTO(Training training);

}