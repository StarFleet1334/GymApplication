package com.demo.folder.service;


import com.demo.folder.entity.base.TrainingType;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.repository.TrainingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeService.class);
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public void createTrainingType(TrainingType trainingType) {
        LOGGER.info("Creating new training type: {}", trainingType.getTrainingTypeName());
        trainingTypeRepository.save(trainingType);
    }

    @Transactional
    public void createTrainingType(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(trainingTypeRequestDTO.getId());
        trainingType.setTrainingTypeName(trainingTypeRequestDTO.getTrainingTypeName());
        createTrainingType(trainingType);
    }

    @Transactional(readOnly = true)
    public List<TrainingType> getAllTrainingTypes() {
        LOGGER.info("Fetching all training types");
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        if (trainingTypes.isEmpty()) {
            throw new EntityNotFoundException("No training types found.");
        }

        return trainingTypes;
    }

    @Transactional(readOnly = true)
    public TrainingTypeRequestDTO getTrainingTypeById(Long id) {
        return trainingTypeRepository.findById(id)
                .map(trainingType -> modelMapper.map(trainingType, TrainingTypeRequestDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Training Type with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeRequestDTO> retrieveAllTrainingTypes() {
        List<TrainingType> list = getAllTrainingTypes();
        if (list.isEmpty()) {
            throw new com.demo.folder.error.exception.EntityNotFoundException("No training types found.");
        }
        return list.stream()
                .map(trainingType -> {
                    TrainingTypeRequestDTO trainingTypeRequestDTO = new TrainingTypeRequestDTO();
                    trainingTypeRequestDTO.setId(trainingType.getId());
                    trainingTypeRequestDTO.setTrainingTypeName(trainingType.getTrainingTypeName());
                    return trainingTypeRequestDTO;
                })
                .collect(Collectors.toList());
    }

}