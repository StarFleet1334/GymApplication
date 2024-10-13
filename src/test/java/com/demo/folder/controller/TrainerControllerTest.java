package com.demo.folder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
class TrainerControllerTest {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
            TrainerControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    private String trainerUsername;
    private String trainerPassword;
    private MockHttpSession session;

    @BeforeEach
    public void setup() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        mockMvc.perform(post("/api/training-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String trainerJson = "{"
                + "\"firstName\": \"TrainerFirstName\","
                + "\"lastName\": \"TrainerLastName\","
                + "\"trainingTypeId\": " + 1
                + "}";

        MvcResult trainerRegistrationResult = mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainerJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
    public void testRegisterTrainer() throws Exception {
        String trainingTypeJson = "{ \"trainingTypeName\": \"Pilates\" }";

        MvcResult trainingTypeResult = mockMvc.perform(post("/api/training-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String trainingTypeResponse = trainingTypeResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();

        String trainerJson = "{"
                + "\"firstName\": \"AnotherTrainer\","
                + "\"lastName\": \"LastName\","
                + "\"trainingTypeId\": " + 2
                + "}";

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainerJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.password", notNullValue()));
    }

    @Test
    public void testGetAllTrainers() throws Exception {
        mockMvc.perform(get("/api/trainers")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username", is(trainerUsername)))
                .andExpect(jsonPath("$[0].trainingTypeId", is(1)));
    }

    @Test
    public void testActivationOfTrainer() throws Exception {
        mockMvc.perform(patch("/api/trainers/{username}/{action}", trainerUsername, "DEACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer de-activated"));

        mockMvc.perform(patch("/api/trainers/{username}/{action}", trainerUsername, "ACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer activated"));

        mockMvc.perform(get("/api/trainers/{username}", trainerUsername)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    public void testDeActivationOfTrainer() throws Exception {
        mockMvc.perform(patch("/api/trainers/{username}/{action}", trainerUsername, "DEACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer de-activated"));

        mockMvc.perform(get("/api/trainers/{username}", trainerUsername)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));
    }


    @Test
    public void testRetrieveTrainerByUserName() throws Exception {
        mockMvc.perform(get("/api/trainers/{username}", trainerUsername)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("TrainerFirstName")))
                .andExpect(jsonPath("$.lastName", is("TrainerLastName")))
                .andExpect(jsonPath("$.specialization", is("Yoga")))
                .andExpect(jsonPath("$.active", is(true)));
    }
}