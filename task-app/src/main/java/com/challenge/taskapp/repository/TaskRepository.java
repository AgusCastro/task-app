package com.challenge.taskapp.repository;

import com.challenge.taskapp.model.Task;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface TaskRepository extends ListCrudRepository<Task, UUID> { }
