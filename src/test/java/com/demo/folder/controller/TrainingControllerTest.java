package com.demo.folder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// Import other necessary annotations and classes

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TrainingControllerTest {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
            TrainingControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    private String traineeUsername;
    private String traineePassword;
    private String trainerUsername;
    private String trainerPassword;
    private MockHttpSession session;

    @BeforeEach
    public void setup() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        MvcResult trainingTypeResult = mockMvc.perform(post("/api/training-type")
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traineeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String traineeRegistrationResponse = traineeRegistrationResult.getResponse()
                .getContentAsString();
        JsonNode traineeJsonNode = objectMapper.readTree(traineeRegistrationResponse);
        traineeUsername = traineeJsonNode.get("username").asText();
        traineePassword = traineeJsonNode.get("password").asText();

        LOGGER.info("Registered trainee with username: {}", traineeUsername);
        LOGGER.info("Registered trainee with password: {}", traineePassword);

        String trainerJson = "{"
                + "\"firstName\": \"TrainerFirstName\","
                + "\"lastName\": \"TrainerLastName\","
                + "\"trainingTypeId\": " + 1
                + "}";

        MvcResult trainerRegistrationResult = mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainerJson))
                .andExpect(status().isCreated())
                .andReturn();

        String trainerRegistrationResponse = trainerRegistrationResult.getResponse()
                .getContentAsString();
        JsonNode trainerJsonNode = objectMapper.readTree(trainerRegistrationResponse);
        trainerUsername = trainerJsonNode.get("username").asText();
        trainerPassword = trainerJsonNode.get("password").asText();

        LOGGER.info("Registered trainer with username: {}", trainerUsername);
        LOGGER.info("Registered trainer with password: {}", trainerPassword);

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", trainerUsername)
                        .param("password", trainerPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andReturn();

        session = (MockHttpSession) loginResult.getRequest().getSession(false);
    }

    @Test
    public void testRegisterTraining() throws Exception {
        String trainingDateStr = "2024/01/01"; // ISO 8601 format

        String trainingJson = "{"
                + "\"traineeUserName\": \"" + traineeUsername + "\","
                + "\"trainerUserName\": \"" + trainerUsername + "\","
                + "\"trainingName\": \"Morning Yoga Session\","
                + "\"trainingDate\": \"" + trainingDateStr + "\","
                + "\"duration\": 60"
                + "}";

        mockMvc.perform(post("/api/trainings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Training created successfully"));
    }

    @Test
    public void testGetTrainings() throws Exception {
        String trainingDateStr = "2024/01/01";

        String trainingJson = "{"
                + "\"traineeUserName\": \"" + traineeUsername + "\","
                + "\"trainerUserName\": \"" + trainerUsername + "\","
                + "\"trainingName\": \"Morning Yoga Session\","
                + "\"trainingDate\": \"" + trainingDateStr + "\","
                + "\"duration\": 60"
                + "}";

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
}