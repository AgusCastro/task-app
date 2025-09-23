package com.challenge.taskapp.dto;

import com.challenge.taskapp.enums.TaskStatus;

public record UpdateTaskRequest(String title, String description, TaskStatus status) { }
