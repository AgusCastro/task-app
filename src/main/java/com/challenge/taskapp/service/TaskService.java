package com.challenge.taskapp.service;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.TaskResponse;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.enums.TaskStatus;
import com.challenge.taskapp.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {

    Page<TaskResponse> getAllPaged(Pageable pageable);
    Page<TaskResponse> getAllByStatus(TaskStatus status, Pageable pageable);
    TaskResponse find(UUID id) throws NotFoundException;
    TaskResponse create(AddTaskRequest addTaskRequest);
    TaskResponse update(UUID uuid, UpdateTaskRequest updateTaskRequest) throws NotFoundException, IllegalArgumentException;
    void delete(UUID id);

}
