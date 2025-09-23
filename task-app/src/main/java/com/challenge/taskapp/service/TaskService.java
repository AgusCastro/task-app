package com.challenge.taskapp.service;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.TaskResponse;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    List<TaskResponse> getAll();
    TaskResponse getById(UUID id) throws NotFoundException;
    TaskResponse create(AddTaskRequest addTaskRequest);
    TaskResponse update(UUID uuid, UpdateTaskRequest updateTaskRequest) throws NotFoundException, IllegalArgumentException;
    void deleteById(UUID id);

}
