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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TraineeControllerTest {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(
            TraineeControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    private String username;
    private String password;
    private MockHttpSession session;

    @BeforeEach
    public void setup() throws Exception {
        String jsonBody = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "address": "Tbilisi Tamarashvili Street"
        }
        """;

        MvcResult registrationResult = mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String registrationResponse = registrationResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
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
    public void testGetAllTrainee() throws Exception {
        mockMvc.perform(get("/api/trainees")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username", is(username)));
    }

    @Test
    public void testActivationOfTrainee() throws Exception {
        mockMvc.perform(patch("/api/trainees/{username}/{action}", username, "DEACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trainee de-activated")));

        mockMvc.perform(patch("/api/trainees/{username}/{action}", username, "ACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trainee activated")));

        mockMvc.perform(get("/api/trainees/{username}/profile", username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    public void testDeActivationOfTrainee() throws Exception {
        mockMvc.perform(patch("/api/trainees/{username}/{action}", username, "DEACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trainee de-activated")));

        mockMvc.perform(get("/api/trainees/{username}/profile", username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active", is(false)));
    }


    @Test
    public void testDeletionOfTrainee() throws Exception {
        mockMvc.perform(delete("/api/trainees/{username}", username)
                        .session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRetrieveTraineeByUserName() throws Exception {
        mockMvc.perform(get("/api/trainees/{username}/profile", username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", equalTo("John")))
                .andExpect(jsonPath("$.lastName", equalTo("Doe")))
                .andExpect(jsonPath("$.address", equalTo("Tbilisi Tamarashvili Street")));
    }
}