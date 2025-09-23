package com.challenge.taskapp.dto;

import com.challenge.taskapp.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateTaskRequest(
        @NotNull(message = "title cannot be null")
        @Length(min = 3, max = 50, message = "title must be between 3 and 50 characters")
        String title,
        @Length(max = 250, message = "description must be at most 250 characters")
        String description,
        TaskStatus status) { }
