package com.challenge.taskapp.dto;

import com.challenge.taskapp.model.Task;

import java.sql.Timestamp;
import java.util.UUID;

public record TaskResponse(UUID id, String title, String description, String status, Timestamp createdAt, Timestamp updatedAt) {
    public TaskResponse(final Task entity) {
        this(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getStatus().name(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
