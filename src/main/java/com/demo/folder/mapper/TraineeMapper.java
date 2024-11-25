package com.demo.folder.mapper;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.CreateTraineeRequestDTO;
import com.demo.folder.entity.dto.response.TraineeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    @Mapping(target = "user", source = "traineeRequestDTO", qualifiedByName = "mapToUser")
    Trainee toEntity(CreateTraineeRequestDTO traineeRequestDTO);

    @Mapping(target = "username", source = "trainee.user.username")
    @Mapping(target = "password", source = "trainee.user.password")
    @Mapping(target = "token", source = "token")
    TraineeResponse toResponse(Trainee trainee, String token);

    @Named("mapToUser")
    default User mapToUser(CreateTraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(traineeRequestDTO.getFirstName());
        user.setLastName(traineeRequestDTO.getLastName());
        return user;
    }

}
