package com.challenge.taskapp.controller;

import com.challenge.taskapp.dto.AddTaskRequest;
import com.challenge.taskapp.dto.TaskResponse;
import com.challenge.taskapp.dto.UpdateTaskRequest;
import com.challenge.taskapp.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable final UUID id) {
        return taskService.getById(id);
    }

    @PostMapping
    public TaskResponse create(@RequestBody final AddTaskRequest newTaskRequest) {
        return taskService.create(newTaskRequest);
    }

    @PutMapping("/{id}")
    public  TaskResponse update(@PathVariable final UUID id, @RequestBody final UpdateTaskRequest updateTaskRequest) {
        return taskService.update(id, updateTaskRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable final UUID id) {
        taskService.deleteById(id);
    }

}
