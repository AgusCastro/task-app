package com.challenge.taskapp.controller;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.enums.TaskStatus;
import com.challenge.taskapp.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(username = "testuser", password = "testpass")
@DisplayName("Task Controller Integration Tests")
class TaskControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return empty page when no tasks exist")
    void shouldReturnEmptyPageWhenNoTasksExist() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @DisplayName("Complete flow: Create, Retrieve, Update, Delete Task")
    void completeFlowCreateRetrieveUpdateDeleteTask() throws Exception {
        // Create Task
        final AddTaskRequest createRequest = new AddTaskRequest("Complete Flow Task", "Testing complete flow");
        final String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Complete Flow Task"))
                .andExpect(jsonPath("$.description").value("Testing complete flow"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final UUID createdTaskId = UUID.fromString(objectMapper.readTree(createResponse).path("id").asText());

        // Retrieve Task - Verify it was created correctly
        mockMvc.perform(get("/tasks/{id}", createdTaskId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTaskId.toString()))
                .andExpect(jsonPath("$.title").value("Complete Flow Task"))
                .andExpect(jsonPath("$.description").value("Testing complete flow"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // Update Task - Change to IN_PROGRESS status
        final UpdateTaskRequest updateRequest = new UpdateTaskRequest("Updated Flow Task", "Updated description for testing", TaskStatus.IN_PROGRESS);
        mockMvc.perform(put("/tasks/{id}", createdTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTaskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Flow Task"))
                .andExpect(jsonPath("$.description").value("Updated description for testing"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Update Task status to DONE
        final UpdateTaskRequest finalUpdateRequest = new UpdateTaskRequest("Completed Flow Task", "Final description", TaskStatus.DONE);
        mockMvc.perform(put("/tasks/{id}", createdTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finalUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTaskId.toString()))
                .andExpect(jsonPath("$.title").value("Completed Flow Task"))
                .andExpect(jsonPath("$.description").value("Final description"))
                .andExpect(jsonPath("$.status").value("DONE"));

        // Delete Task
        mockMvc.perform(delete("/tasks/{id}", createdTaskId))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify Task is deleted - should return 404
        mockMvc.perform(get("/tasks/{id}", createdTaskId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter tasks by status")
    void shouldFilterTasksByStatus() throws Exception {
        // Given
        final UUID task1Id = createTestTask("Task1", "Description1");
        final UUID task2Id = createTestTask("Task2", "Description2");

        // Update task2 to IN_PROGRESS
        updateTaskStatus(task2Id, "Task2", "Description2", TaskStatus.IN_PROGRESS);

        // When & Then
        mockMvc.perform(get("/tasks")
                        .param("status", "PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.content[0].id").value(task1Id.toString()));
    }

    @Nested
    @DisplayName("GET /tasks/{id}")
    class GetTaskByIdTests {

        @Test
        @DisplayName("Should return task when valid ID is provided")
        void shouldReturnTaskWhenValidIdProvided() throws Exception {
            // Given
            final String taskTitle = "Test Task";
            final String taskDescription = "Test Description";
            final UUID taskId = createTestTask(taskTitle, taskDescription);

            // When & Then
            mockMvc.perform(get("/tasks/{id}", taskId.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(taskId.toString()))
                    .andExpect(jsonPath("$.title").value(taskTitle))
                    .andExpect(jsonPath("$.description").value(taskDescription))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("Should return 404 when task not found")
        void shouldReturn404WhenTaskNotFound() throws Exception {
            // Given
            final UUID nonExistentId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/tasks/{id}", nonExistentId))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /tasks")
    class CreateTaskTests {

        @Test
        @DisplayName("Should return 400 when title is null")
        void shouldReturn400WhenTitleIsNull() throws Exception {
            // Given
            final AddTaskRequest request = new AddTaskRequest(null, "Description");

            // When & Then
            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when title is too long")
        void shouldReturn400WhenTitleTooLong() throws Exception {
            // Given
            final String longTitle = "A".repeat(51);
            final AddTaskRequest request = new AddTaskRequest(longTitle, "Description");

            // When & Then
            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create task with null description")
        void shouldCreateTaskWithNullDescription() throws Exception {
            // Given
            final AddTaskRequest request = new AddTaskRequest("Valid Title", null);

            // When & Then
            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Valid Title"))
                    .andExpect(jsonPath("$.description").isEmpty());
        }
    }

    @Nested
    @DisplayName("PUT /tasks/{id}")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update task with valid request")
        void shouldUpdateTaskWithValidRequest() throws Exception {
            // Given
            final UUID taskId = createTestTask("Original Title", "Original Description");

            final UpdateTaskRequest request = new UpdateTaskRequest("Updated Title", "Updated Description", TaskStatus.IN_PROGRESS);

            // When & Then
            mockMvc.perform(put("/tasks/{id}", taskId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(taskId.toString()))
                    .andExpect(jsonPath("$.title").value("Updated Title"))
                    .andExpect(jsonPath("$.description").value("Updated Description"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @DisplayName("Should return 400 when update to invalid status")
        void shouldReturn400WhenUpdateInvalidStatus() throws Exception {
            // Given
            final UUID taskId = createTestTask("Original Title", "Original Description");

            // Tasks can not be updated directly to DONE
            final UpdateTaskRequest request = new UpdateTaskRequest("Updated Title", "Updated Description", TaskStatus.DONE);

            // When & Then
            mockMvc.perform(put("/tasks/{id}", taskId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent task")
        void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
            // Given
            final UUID nonExistentId = UUID.randomUUID();
            final UpdateTaskRequest request = new UpdateTaskRequest("Title", "Description", TaskStatus.DONE);

            // When & Then
            mockMvc.perform(put("/tasks/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when update request is invalid")
        void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
            // Given
            final UUID taskId = createTestTask("Task to Update", "Original Description");

            final UpdateTaskRequest request = new UpdateTaskRequest(null, "Description", TaskStatus.DONE);

            // When & Then
            mockMvc.perform(put("/tasks/{id}", taskId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /tasks/{id}")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delete task when valid ID is provided")
        void shouldDeleteTaskWhenValidIdProvided() throws Exception {
            // Given
            final UUID taskId = createTestTask("Task to Delete", "Description");

            // When & Then
            mockMvc.perform(delete("/tasks/{id}", taskId.toString()))
                    .andDo(print())
                    .andExpect(status().isOk());

            // Verify task is deleted
            mockMvc.perform(get("/tasks/{id}", taskId.toString()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent task")
        void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
            // Given
            final UUID nonExistentId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/tasks/{id}", nonExistentId))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
