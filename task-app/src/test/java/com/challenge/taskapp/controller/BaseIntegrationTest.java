package com.challenge.taskapp.controller;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.enums.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class contains the base operations for integration tests.
 */
public class BaseIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    UUID createTestTask(final String title, final String description) throws Exception {
        final AddTaskRequest request = new AddTaskRequest(title, description);
        final String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(createResponse).path("id").asText());
    }

    void updateTaskStatus(final UUID taskId, final String title, final String description, final TaskStatus status) throws Exception {
        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTaskRequest(title, description, status))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.status").value(status.toString()));
    }


}
