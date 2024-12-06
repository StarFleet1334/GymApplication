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
class TraineeControllerTest {


    @Autowired
    private MockMvc mockMvc;

    private String username;
    private String token;
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

        String registrationJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "address": "123 Main Street"
        }
        """;

        MvcResult registrationResult = mockMvc.perform(post("/api/trainees")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registrationResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(registrationResponse);
        this.username = jsonNode.get("username").asText();
        this.token = jsonNode.get("token").asText();

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", this.username)
                        .param("password", this.token))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andReturn();

        session = (MockHttpSession) loginResult.getRequest().getSession(false);
    }

    @Test
    void testGetAllTrainee() throws Exception {
        mockMvc.perform(get("/api/trainees")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username", is(this.username)));
    }

    @Test
    void testActivationOfTrainee() throws Exception {
        mockMvc.perform(patch("/api/trainees/{username}/{action}", this.username, "DEACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trainee de-activated")));

        mockMvc.perform(patch("/api/trainees/{username}/{action}", this.username, "ACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trainee activated")));

        mockMvc.perform(get("/api/trainees/{username}/profile", this.username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    void testDeActivationOfTrainee() throws Exception {
        mockMvc.perform(patch("/api/trainees/{username}/{action}", this.username, "DEACTIVATE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trainee de-activated")));

        mockMvc.perform(get("/api/trainees/{username}/profile", this.username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    void testDeletionOfTrainee() throws Exception {
        mockMvc.perform(delete("/api/trainees/{username}", this.username)
                        .session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateTrainee() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        String updateJson = """
                {
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "isActive": true
                }
                """;

        mockMvc.perform(put("/api/trainees/{username}", this.username)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/trainees/{username}/profile", this.username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", equalTo("Jane")))
                .andExpect(jsonPath("$.lastName", equalTo("Doe")));
    }

    @Test
    void testRetrieveTraineeByUserName() throws Exception {
        mockMvc.perform(get("/api/trainees/{username}/profile", this.username)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", equalTo("John")))
                .andExpect(jsonPath("$.lastName", equalTo("Doe")))
                .andExpect(jsonPath("$.address", equalTo("123 Main Street")));
    }
}