package com.challenge.taskapp.repository;

import com.challenge.taskapp.enums.TaskStatus;
import com.challenge.taskapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TaskRepository extends CrudRepository<Task, UUID>, PagingAndSortingRepository<Task, UUID> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}
