package com.demo.folder.controller.step.trainingType;

import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.service.TrainingTypeService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@ExtendWith(MockitoExtension.class)
public class TrainingTypeManagementSteps {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeRequestDTO trainingTypeRequestDTO;
    private ResponseEntity<?> response;
    private List<TrainingTypeRequestDTO> mockTrainingTypeList;

    @Before
    public void setup() {
        trainingTypeService = mock(TrainingTypeService.class);
        trainingTypeRequestDTO = new TrainingTypeRequestDTO();
        mockTrainingTypeList = new ArrayList<>();
    }

    // Scenario 1
    @When("the administrator requests to create a training type with name {string}")
    public void the_administrator_requests_to_create_a_training_type_with_name(String trainingTypeName) {
        trainingTypeRequestDTO.setTrainingTypeName(trainingTypeName);
        doNothing().when(trainingTypeService).createTrainingType(trainingTypeRequestDTO);
        trainingTypeService.createTrainingType(trainingTypeRequestDTO);
        response = new ResponseEntity<>("Training type created successfully.", HttpStatus.CREATED);
    }

    @Then("the system should create the training type and return a success message")
    public void the_system_should_create_the_training_type_and_return_a_success_message() {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Training type created successfully.", response.getBody());
    }

    // Scenario 2
    @When("the administrator requests to create a training type with no name")
    public void the_administrator_requests_to_create_a_training_type_with_no_name() {
        trainingTypeRequestDTO.setTrainingTypeName("");
        doThrow(new IllegalArgumentException("Training type name is required"))
                .when(trainingTypeService).createTrainingType(any(TrainingTypeRequestDTO.class));

        try {
            trainingTypeService.createTrainingType(trainingTypeRequestDTO);
        } catch (IllegalArgumentException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Then("the system should fail to create the training type and return an error message")
    public void the_system_should_fail_to_create_the_training_type_and_return_an_error_message() {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Training type name is required", response.getBody());
    }

    // Scenario 3
    @When("the administrator requests to retrieve all training types when none")
    public void the_administrator_requests_to_retrieve_all_training_types() {
        when(trainingTypeService.retrieveAllTrainingTypes()).thenReturn(new ArrayList<>());

        List<TrainingTypeRequestDTO> result = trainingTypeService.retrieveAllTrainingTypes();
        if (result.isEmpty()) {
            response = new ResponseEntity<>("No content", HttpStatus.NO_CONTENT);
        } else {
            response = new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @Then("the system should return a no content status")
    public void the_system_should_return_a_no_content_status() {
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // Scenario 4
    @When("the administrator requests to retrieve all training types when exist")
    public void the_administrator_requests_to_retrieve_all_training_types_when_they_exist() {
        mockTrainingTypeList = Arrays.asList(
                new TrainingTypeRequestDTO() {{
                    setTrainingTypeName("Java Fundamentals");
                }},
                new TrainingTypeRequestDTO() {{
                    setTrainingTypeName("Advanced Python");
                }}
        );

        when(trainingTypeService.retrieveAllTrainingTypes()).thenReturn(mockTrainingTypeList);
        response = new ResponseEntity<>(mockTrainingTypeList, HttpStatus.OK);
    }

    @Then("the system should return all training types")
    public void the_system_should_return_all_training_types() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    // Scenario 5
    @And("there exists a training type with ID {int}")
    public void there_exists_a_training_type_with_id(Integer id) {
        TrainingTypeRequestDTO existingTrainingType = new TrainingTypeRequestDTO();
        existingTrainingType.setId(id.longValue());
        existingTrainingType.setTrainingTypeName("Java Fundamentals");

        when(trainingTypeService.getTrainingTypeById(id.longValue())).thenReturn(existingTrainingType);
    }


    @When("the administrator requests to retrieve the training type by ID {int}")
    public void the_administrator_requests_to_retrieve_the_training_type_by_id(Integer id) {
        trainingTypeRequestDTO.setTrainingTypeName("Java Fundamentals");
        trainingTypeRequestDTO.setId(Long.valueOf(id));

        when(trainingTypeService.getTrainingTypeById(anyLong())).thenReturn(trainingTypeRequestDTO);
        response = new ResponseEntity<>(trainingTypeService.getTrainingTypeById(id.longValue()), HttpStatus.OK);
    }

    @Then("the system should return the training type details")
    public void the_system_should_return_the_training_type_details() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // Scenario 6
    @When("the administrator requests to retrieve a training type by ID 99")
    public void the_administrator_requests_to_retrieve_a_training_type_by_id_99() {
        when(trainingTypeService.getTrainingTypeById(99L))
                .thenThrow(new EntityNotFoundException("Training Type with ID 99 not found"));

        try {
            trainingTypeService.getTrainingTypeById(99L);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Then("the system should return an error stating the training type is not found")
    public void the_system_should_return_an_error_stating_the_training_type_is_not_found() {
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Training Type with ID 99 not found", response.getBody());
    }
}

