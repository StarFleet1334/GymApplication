package com.demo.folder.client;

import com.demo.folder.config.FeignClientConfig;
import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "workload", configuration = FeignClientConfig.class)
public interface SecondaryMicroserviceClient {

    @PostMapping("/trainings")
    void addTraining(@RequestBody TrainingSessionDTO trainingSessionDTO);

    @DeleteMapping("/trainings")
    void deleteTraining(@RequestBody TrainingSessionDTO trainingSessionDTO);
}
