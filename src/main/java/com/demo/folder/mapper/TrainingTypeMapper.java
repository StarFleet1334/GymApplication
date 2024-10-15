package com.demo.folder.mapper;

import com.demo.folder.entity.base.TrainingType;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainingTypeMapper {
    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainingTypeName", target = "trainingTypeName")
    TrainingType toEntity(TrainingTypeRequestDTO trainingTypeRequestDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainingTypeName", target = "trainingTypeName")
    TrainingTypeRequestDTO toDTO(TrainingType trainingType);
}