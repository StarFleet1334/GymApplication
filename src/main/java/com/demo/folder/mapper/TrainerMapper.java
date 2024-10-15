package com.demo.folder.mapper;

import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.response.TrainerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "user", source = "trainerRequestDTO", qualifiedByName = "mapToUser")
    @Mapping(target = "trainees", ignore = true)
    Trainer toEntity(TrainerRequestDTO trainerRequestDTO);

    @Mapping(target = "username", source = "trainer.user.username")
    @Mapping(target = "password", source = "trainer.user.password")
    @Mapping(target = "token", source = "token")
    TrainerResponseDTO toResponse(Trainer trainer, String token);

    @Named("mapToUser")
    default User mapToUser(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(trainerRequestDTO.getFirstName());
        user.setLastName(trainerRequestDTO.getLastName());
        return user;
    }

}