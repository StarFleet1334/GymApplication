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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TrainingTypeControllerTest {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
            TrainingTypeControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    private String username;
    private String password;
    private MockHttpSession session;

    @BeforeEach
    public void setup() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String registrationJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "address": "123 Main Street"
        }
        """;

        MvcResult registrationResult = mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registrationResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(registrationResponse);
        username = jsonNode.get("username").asText();
        password = jsonNode.get("password").asText();

        LOGGER.info("Registered trainee with username: {}", username);
        LOGGER.info("Registered trainee with password: {}", password);

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andReturn();

        session = (MockHttpSession) loginResult.getRequest().getSession(false);
    }

    @Test
    public void testRegisterTrainingType() throws Exception {
        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        MvcResult result = mockMvc.perform(post("/api/training-type")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    public void testGetAllTrainingTypes() throws Exception {
        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        mockMvc.perform(post("/api/training-type")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/training-type")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].trainingTypeName", is("Yoga")));
    }

    @Test
    public void testGetTrainingTypeById() throws Exception {
        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        mockMvc.perform(post("/api/training-type")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(get("/api/training-type/{id}", 1)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingTypeName", is("Yoga")))
                .andExpect(jsonPath("$.id", is(1)));
    }
}