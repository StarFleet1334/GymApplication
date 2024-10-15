package com.demo.folder.controller;

import com.demo.folder.entity.dto.request.UserCredentials;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
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
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                        "$[0].trainingTypeName", is("Yoga")));
    }

    @Test
    public void testGetTrainingTypeById() throws Exception {
        String trainingTypeJson = "{ \"trainingTypeName\": \"Yoga\" }";

        mockMvc.perform(post("/api/training-type")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingTypeJson))
                .andExpect(status().isCreated());

        MvcResult getAllResult = mockMvc.perform(get("/api/training-type")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String getAllResponse = getAllResult.getResponse().getContentAsString();
        JsonNode arrayNode = objectMapper.readTree(getAllResponse);
        Long id = arrayNode.get(0).get("id").asLong();

        mockMvc.perform(get("/api/training-type/" + id)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                        "$.trainingTypeName", is("Yoga")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                        "$.id", is(id.intValue())));
    }

    @Test
    public void testLogout() throws Exception {
        UserCredentials credentials = new UserCredentials();
        credentials.setUsername("admin");
        credentials.setPassword("admin");

        String entityJson = new ObjectMapper().writeValueAsString(credentials);

        mockMvc.perform(post("/api/logout")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entityJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));
    }

    @Test
    public void testUnsuccessfulLoginAttemptsBlockUser() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("username", "admin")
                            .param("password", "wrongpassword"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Invalid credentials"));
        }
        mockMvc.perform(post("/api/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "admin")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User is blocked due to multiple failed login attempts."));
    }

}