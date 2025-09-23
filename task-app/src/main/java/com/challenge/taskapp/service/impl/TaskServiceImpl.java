package com.challenge.taskapp.service.impl;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.TaskResponse;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.enums.TaskStatus;
import com.challenge.taskapp.exception.NotFoundException;
import com.challenge.taskapp.model.Task;
import com.challenge.taskapp.repository.TaskRepository;
import com.challenge.taskapp.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public final class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Page<TaskResponse> getAllPaged(Pageable pageable) {
        return taskRepository.findAll(pageable).map(TaskResponse::new);
    }

    @Override
    public Page<TaskResponse> getAllByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable).map(TaskResponse::new);
    }

    @Override
    public TaskResponse getById(final UUID id) {
        return taskRepository.findById(id).map(TaskResponse::new).orElseThrow(NotFoundException::new);
    }

    @Override
    public TaskResponse update(final UUID uuid, final UpdateTaskRequest task) {
        if (uuid == null || task == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        final Task existing = taskRepository.findById(uuid).orElseThrow(NotFoundException::new);

        if (task.title() != null) {
            existing.setTitle(task.title());
        }
        if (task.description() != null) {
            existing.setDescription(task.description());
        }

        if (task.status() != null) {
            existing.setStatus(task.status());
        }

        return new TaskResponse(taskRepository.save(existing));
    }

    @Override
    public TaskResponse create(final AddTaskRequest taskRequest) {
        final Task taskCreated = new Task(taskRequest.title(), taskRequest.description());
        return new TaskResponse(taskRepository.save(taskCreated));
    }

    @Override
    public void deleteById(final UUID id) {
        taskRepository.findById(id).orElseThrow(NotFoundException::new);
        taskRepository.deleteById(id);
    }
}
