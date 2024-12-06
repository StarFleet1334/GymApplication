package com.demo.folder.controller;

import com.demo.folder.controller.implementation.TrainingController;
import com.demo.folder.entity.base.TrainingSession;
import com.demo.folder.entity.dto.request.TrainingSessionDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.error.exception.ErrorResponse;
import com.demo.folder.service.TrainingSessionService;
import com.demo.folder.utils.EntityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TrainingControllerTest {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TrainingControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TrainingSessionService trainingSessionService;

    @InjectMocks
    private TrainingController trainingController;

    private String traineeUsername;
    private String traineePassword;
    private String trainerUsername;
    private String trainerPassword;
    private MockHttpSession session;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        this.session = new MockHttpSession();
        this.objectMapper = new ObjectMapper();

        MvcResult adminResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome admin"))
                .andReturn();

        session = (MockHttpSession) adminResult.getRequest().getSession(false);
        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        MvcResult trainingTypeResult = mockMvc.perform(post("/api/training-type")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String traineeJson = """
        {
            "firstName": "TraineeFirstName",
            "lastName": "TraineeLastName",
            "dateOfBirth": "1995-05-15",
            "address": "123 Trainee Street"
        }
        """;

        MvcResult traineeRegistrationResult = mockMvc.perform(post("/api/trainees")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traineeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String traineeRegistrationResponse = traineeRegistrationResult.getResponse().getContentAsString();
        JsonNode traineeJsonNode = objectMapper.readTree(traineeRegistrationResponse);
        traineeUsername = traineeJsonNode.get("username").asText();
        traineePassword = traineeJsonNode.get("token").asText();

        LOGGER.info("Registered trainee with username: {}", traineeUsername);
        LOGGER.info("Registered trainee with password: {}", traineePassword);

        String trainerJson = """
        {
            "firstName": "TrainerFirstName",
            "lastName": "TrainerLastName",
            "trainingTypeId": %s
        }
        """.formatted(1);

        MvcResult trainerRegistrationResult = mockMvc.perform(post("/api/trainers")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainerJson))
                .andExpect(status().isCreated())
                .andReturn();

        String trainerRegistrationResponse = trainerRegistrationResult.getResponse().getContentAsString();
        JsonNode trainerJsonNode = objectMapper.readTree(trainerRegistrationResponse);
        trainerUsername = trainerJsonNode.get("username").asText();
        trainerPassword = trainerJsonNode.get("token").asText();

        LOGGER.info("Registered trainer with username: {}", trainerUsername);
        LOGGER.info("Registered trainer with password: {}", trainerPassword);

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", trainerUsername)
                        .param("password", trainerPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andReturn();

        session = (MockHttpSession) loginResult.getRequest().getSession(false);
        MvcResult adminResult1 = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome admin"))
                .andReturn();

        session = (MockHttpSession) adminResult1.getRequest().getSession(false);
    }

    @Test
    void testRegisterTraining() throws Exception {

        String trainingDateStr = "2024/10/14";

        String trainingJson = """
        {
            "traineeUserName": "%s",
            "trainerUserName": "%s",
            "trainingName": "Morning Yoga Session",
            "trainingDate": "%s",
            "duration": 60
        }
        """.formatted(traineeUsername, trainerUsername, trainingDateStr);

        mockMvc.perform(post("/api/trainings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Training created successfully"));
    }

    @Test
    void testGetTrainings() throws Exception {
        String trainingDateStr = "2024/10/14";

        String trainingJson = """
        {
            "traineeUserName": "%s",
            "trainerUserName": "%s",
            "trainingName": "Morning Yoga Session",
            "trainingDate": "%s",
            "duration": 60
        }
        """.formatted(traineeUsername, trainerUsername, trainingDateStr);

        mockMvc.perform(post("/api/trainings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/trainings")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].trainingName", is("Morning Yoga Session")));
    }

    @Test
    void testCreateTrainingSessionSuccess() {
        TrainingSessionDTO trainingSessionDTO = new TrainingSessionDTO();

        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setId(1L);

        when(trainingSessionService.createTrainingSession(any(TrainingSessionDTO.class))).thenReturn(trainingSession);

        ResponseEntity<Object> response = trainingController.createTrainingSession(trainingSessionDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getHeaders().getLocation().toString().contains("/1"));
        assertEquals("Training session created successfully", response.getBody());
    }

    @Test
    void testDeleteTrainingSessionSuccess() {
        Long id = 1L;

        ResponseEntity<Void> response = trainingController.deleteTrainingSession(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteTrainingSessionEntityNotFound() {
        Long id = 1L;
        doThrow(new EntityNotFoundException("TrainingSession not found"))
                .when(trainingSessionService).deleteTrainingSession(id);

        ResponseEntity<Void> response = trainingController.deleteTrainingSession(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}