package com.challenge.taskapp.service.impl;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.TaskResponse;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.enums.TaskStatus;
import com.challenge.taskapp.exception.NotFoundException;
import com.challenge.taskapp.model.Task;
import com.challenge.taskapp.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
final class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void testGetAllPaged() {
        final UUID taskId = UUID.randomUUID();
        final Timestamp now = Timestamp.from(Instant.now());
        final Task task = new Task(taskId, "Test Task", "Test Description", TaskStatus.PENDING, now, now, "tenant1");

        final Pageable pageable = PageRequest.of(0, 10);
        final Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        Mockito.when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        final Page<TaskResponse> response = taskService.getAllPaged(pageable);

        Assertions.assertEquals(1, response.getTotalElements());
        Assertions.assertEquals(taskId, response.getContent().get(0).id());
        Assertions.assertEquals("Test Task", response.getContent().get(0).title());

        Mockito.verify(taskRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void testGetAllByStatus() {
        final UUID taskId = UUID.randomUUID();
        final Timestamp now = Timestamp.from(Instant.now());
        final Task task = new Task(taskId, "Test Task", "Test Description", TaskStatus.IN_PROGRESS, now, now, "tenant1");

        final Pageable pageable = PageRequest.of(0, 10);
        final Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        Mockito.when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS, pageable)).thenReturn(taskPage);

        final Page<TaskResponse> response = taskService.getAllByStatus(TaskStatus.IN_PROGRESS, pageable);

        Assertions.assertEquals(1, response.getTotalElements());
        Assertions.assertEquals(taskId, response.getContent().get(0).id());
        Assertions.assertEquals("IN_PROGRESS", response.getContent().get(0).status());

        Mockito.verify(taskRepository, Mockito.times(1)).findByStatus(TaskStatus.IN_PROGRESS, pageable);
    }

    @Test
    void testFind() {
        final UUID taskId = UUID.randomUUID();
        final Timestamp now = Timestamp.from(Instant.now());
        final Task task = new Task(taskId, "Test Task", "Test Description", TaskStatus.PENDING, now, now, "tenant1");

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        final TaskResponse response = taskService.find(taskId);

        Assertions.assertEquals(taskId, response.id());
        Assertions.assertEquals("Test Task", response.title());
        Assertions.assertEquals("Test Description", response.description());
        Assertions.assertEquals("PENDING", response.status());

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
    }

    @Test
    void testFind_NotFound() {
        final UUID taskId = UUID.randomUUID();

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> taskService.find(taskId));

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
    }

    @Test
    void testUpdate() {
        final UUID taskId = UUID.randomUUID();
        final Timestamp now = Timestamp.from(Instant.now());
        final Task existingTask = new Task(taskId, "Old Title", "Old Description", TaskStatus.PENDING, now, now, "tenant1");
        final Task updatedTask = new Task(taskId, "New Title", "New Description", TaskStatus.IN_PROGRESS, now, now, "tenant1");

        final UpdateTaskRequest updateRequest = new UpdateTaskRequest("New Title", "New Description", TaskStatus.IN_PROGRESS);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenAnswer(arg -> arg.getArgument(0));

        final TaskResponse response = taskService.update(taskId, updateRequest);

        Assertions.assertEquals(updatedTask.getId(), response.id());
        Assertions.assertEquals(updatedTask.getTitle(), response.title());
        Assertions.assertEquals(updatedTask.getDescription(), response.description());
        Assertions.assertEquals(updatedTask.getStatus().toString(), response.status());

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(taskRepository, Mockito.times(1)).save(existingTask);
    }

    @Test
    void testUpdate_NotFound() {
        final UUID taskId = UUID.randomUUID();
        final UpdateTaskRequest updateRequest = new UpdateTaskRequest("New Title", "New Description", TaskStatus.IN_PROGRESS);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> taskService.update(taskId, updateRequest));

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    void testUpdate_NullId() {
        final UpdateTaskRequest updateRequest = new UpdateTaskRequest("New Title", "New Description", TaskStatus.IN_PROGRESS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.update(null, updateRequest));

        Mockito.verify(taskRepository, Mockito.never()).findById(Mockito.any(UUID.class));
    }

    @Test
    void testUpdate_NullRequest() {
        final UUID taskId = UUID.randomUUID();

        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.update(taskId, null));

        Mockito.verify(taskRepository, Mockito.never()).findById(Mockito.any(UUID.class));
    }

    @Test
    void testCreate() {
        final UUID taskId = UUID.randomUUID();
        final Timestamp now = Timestamp.from(Instant.now());
        final AddTaskRequest addRequest = new AddTaskRequest("New Task", "Task Description");
        final String expectedTitle = "New Task";
        final String expectedDescription = "Task Description";
        final String expectedStatus = "PENDING";
        final Task savedTask = new Task(taskId, "New Task", "Task Description", TaskStatus.PENDING, now, now, "tenant1");

        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(savedTask);

        final TaskResponse response = taskService.create(addRequest);

        Assertions.assertEquals(taskId, response.id());
        Assertions.assertEquals(expectedTitle, response.title());
        Assertions.assertEquals(expectedDescription, response.description());
        Assertions.assertEquals(expectedStatus, response.status());

        Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any(Task.class));
    }

    @Test
    void testDelete() {
        final UUID taskId = UUID.randomUUID();
        final Task task = Mockito.mock(Task.class);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.delete(taskId);

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(taskId);
    }

    @Test
    void testDelete_NotFound() {
        final UUID taskId = UUID.randomUUID();

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> taskService.delete(taskId));

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);
        Mockito.verify(taskRepository, Mockito.never()).deleteById(taskId);
    }
}
