package com.challenge.taskapp.controller;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.TaskResponse;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.enums.TaskStatus;
import com.challenge.taskapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Page<TaskResponse> getAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) TaskStatus status) {
        if (status != null) {
            return taskService.getAllByStatus(status, pageable);
        }
        return taskService.getAllPaged(pageable);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable final UUID id) {
        return taskService.find(id);
    }

    @PostMapping
    public TaskResponse create(@RequestBody @Valid final AddTaskRequest newTaskRequest) {
        return taskService.create(newTaskRequest);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable final UUID id, @RequestBody @Valid final UpdateTaskRequest updateTaskRequest) {
        return taskService.update(id, updateTaskRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable final UUID id) {
        taskService.delete(id);
    }
}
